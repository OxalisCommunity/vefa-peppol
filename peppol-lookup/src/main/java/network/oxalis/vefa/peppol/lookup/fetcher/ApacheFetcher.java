/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package network.oxalis.vefa.peppol.lookup.fetcher;

import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import network.oxalis.vefa.peppol.lookup.api.*;
import network.oxalis.vefa.peppol.mode.Mode;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

/**
 * HTTP-based metadata fetcher using Apache HttpClient 5 with connection pooling.
 *
 * <p>Fetches SMP metadata from one or more URIs, trying each in order until one succeeds.
 * Maps HTTP response codes and network errors to specific exception subtypes so that
 * operators can make informed retry vs fallback decisions.</p>
 *
 * <p>Exception mapping at this (HTTP/SMP) layer:</p>
 * <ul>
 *   <li>HTTP 404 → {@link PeppolResourceException} — resource definitively not found</li>
 *   <li>HTTP 429/5xx → {@link PeppolInfrastructureException} — SMP server issue, retry may help</li>
 *   <li>Timeout/socket/DNS errors → {@link NetworkFailureException} — transport-level failure</li>
 *   <li>Other HTTP codes → {@link LookupException} — unclassified, logged for investigation</li>
 * </ul>
 *
 * <p><b>Design note on {@link UnknownHostException}:</b> at this HTTP layer, a DNS resolution
 * failure is ambiguous — it could be a local resolver issue or the SMP host being unreachable.
 * This is distinct from the SML/DNS layer ({@link network.oxalis.vefa.peppol.lookup.locator.BdxlLocator}),
 * where DNS is the native protocol and result codes carry definitive meaning.
 * See {@link NetworkFailureException} for the rationale.</p>
 *
 * @see PeppolResourceException
 * @see PeppolInfrastructureException
 * @see NetworkFailureException
 */
@Slf4j
public class ApacheFetcher extends BasicApacheFetcher implements AutoCloseable {

    private volatile CloseableHttpClient httpClient;
    private final PoolingHttpClientConnectionManager httpClientConnectionManager;

    public ApacheFetcher(Mode mode) {
        super(mode);
        this.httpClientConnectionManager = new PoolingHttpClientConnectionManager();
    }

    protected CloseableHttpClient createClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(httpClientConnectionManager)
                .setConnectionManagerShared(true)
                .build();
    }


    /**
     * Returns the shared HTTP client, creating it lazily on first use.
     *
     * <p>Lazy initialization avoids calling the virtual method {@link #createClient()}
     * from the constructor, which breaks subclasses (OxalisApacheFetcher) that depends on post-construction
     * field initialization</p>
     */
    protected CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    httpClient = createClient();
                }
            }
        }
        return httpClient;
    }

    /**
     * Attempts to fetch metadata from each URI in order, returning the first successful response.
     * If all URIs fail, the last exception is re-thrown — preserving its specific subtype
     * so the caller knows whether to retry or fall back.
     *
     * @param uriList one or more SMP metadata URIs to try in order
     * @return the fetched metadata response from the first successful URI
     * @throws PeppolResourceException       if the resource was definitively not found (HTTP 404)
     * @throws PeppolInfrastructureException if the SMP server returned an error (HTTP 429/5xx)
     * @throws NetworkFailureException       if a transport-level failure occurred (timeout, socket, DNS)
     * @throws LookupException               if the URI list is null or an unclassified error occurred
     */
    public FetcherResponse fetch(List<URI> uriList) throws LookupException {

        if (uriList == null)
            throw new LookupException("Provided URI list is null. Cannot perform metadata lookup.");

        LookupException lastException = null;

        for (URI uri : uriList) {
            try {
                FetcherResponse fetcherResponse = fetchResponseFromValidUri(uri);
                if (fetcherResponse != null) {
                    return fetcherResponse;
                }
            } catch (LookupException e) {
                log.debug("Error fetching metadata from URI: {}. Exception: {}", uri, e.getMessage());
                lastException = e;
            }
        }

        throw (lastException != null) ? lastException : new LookupException("Could not fetch metadata from any of the provided URIs.");

    }

    /**
     * Fetches metadata from a single URI, mapping HTTP status codes and low-level
     * network exceptions to the appropriate domain-specific subtypes.
     *
     * @throws PeppolResourceException       for HTTP 404 — participant/doc type not found at SMP
     * @throws PeppolInfrastructureException for HTTP 429 (rate limit) and 5xx (server failures)
     * @throws NetworkFailureException       for timeouts, connection resets, and DNS resolution failures
     * @throws LookupException               for any other I/O error
     */
    private FetcherResponse fetchResponseFromValidUri(URI uri) throws LookupException {

        HttpGet httpGet = new HttpGet(uri);

        try (CloseableHttpResponse response = (CloseableHttpResponse) getHttpClient().executeOpen(null, httpGet, null)) {
            // handle the response based on the status code and content
            return handleResponse(uri, response);
        } catch (SocketTimeoutException e) {
            throw new NetworkFailureException("Connection timed out while fetching metadata from URI: " + uri, e);
        } catch (SocketException e) {
            throw new NetworkFailureException("Socket error while fetching metadata from URI: " + uri, e);
        } catch (UnknownHostException e) {
            throw new NetworkFailureException("SMP not found or cannot be resolved while fetching metadata from URI: " + uri, e);
        } catch (IOException e) {
            throw new LookupException("I/O error while fetching metadata from URI: " + uri, e);
        }
    }

    /**
     * Maps HTTP status codes to domain-specific exception types.
     *
     * @throws PeppolResourceException       for HTTP 404 — participant/doc type not found at SMP
     * @throws PeppolInfrastructureException for HTTP 429 (rate limit) and 5xx (server failures)
     * @throws LookupException               for any other unexpected HTTP status code
     */
    private static FetcherResponse handleResponse(URI uri, CloseableHttpResponse response) throws LookupException {
        int statusCode = response.getCode();
        switch (statusCode) {
            // Successful response, transform it into FetcherResponse
            case 200:
                return transformResponse(response, uri);

            // SMP Metadata not found, throw resource exception
            case 404:
                throw new PeppolResourceException("Participant metadata not found at URI: " + uri);

                // Hitting SMP rate limit
            case 429:
                throw new PeppolInfrastructureException("Too many requests to SMP server at URI: " + uri + ". Please retry after some time.");

                // server-side failures => PEPPOL infrastructure issues, throw infrastructure exception
            case 500:
            case 502:
            case 503:
            case 504:
                throw new PeppolInfrastructureException("SMP server error (HTTP " + statusCode + ") while fetching metadata from URI: " + uri);

            default:
                throw new LookupException(String.format("Received HTTP status code %s for lookup at URI: %s", statusCode, uri));
        }
    }


    /**
     * Transforms a successful HTTP response into a {@link FetcherResponse},
     * extracting the body content and the optional {@code X-SMP-Namespace} header.
     */
    private static FetcherResponse transformResponse(CloseableHttpResponse response, URI uri) throws LookupException {

        if (response.getEntity() == null) {
            throw new LookupException("Received empty response for metadata lookup at URI: " + uri);
        }

        try (InputStream inputStream = response.getEntity().getContent()) {
            return FetcherResponse.builder()
                    .inputStream(new ByteArrayInputStream(ByteStreams.toByteArray(inputStream)))
                    .namespace(response.containsHeader("X-SMP-Namespace") ?
                            response.getFirstHeader("X-SMP-Namespace").getValue() :
                            null
                    ).build();
        } catch (IOException e) {
            throw new LookupException("Error reading response content for metadata lookup at URI: " + uri, e);
        }
    }

    @Override
    public void close() throws Exception {
        // synchronize to ensure thread safety when closing the shared HTTP client and connection manager
        // because connection manager is shared.
        synchronized (this) {
            // close http client if it was created
            if (httpClient != null) httpClient.close();

            // close the connection manager to release all resources
            httpClientConnectionManager.close();
        }
    }
}
