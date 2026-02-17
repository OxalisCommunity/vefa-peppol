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

import lombok.extern.slf4j.Slf4j;
import network.oxalis.vefa.peppol.lookup.api.*;
import network.oxalis.vefa.peppol.mode.Mode;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.*;
import java.util.List;

@Slf4j
public class UrlFetcher extends AbstractFetcher {

    public UrlFetcher(Mode mode) {
        super(mode);
    }

    @Override
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

        throw (lastException != null) ? lastException
                : new LookupException("Could not fetch metadata from any of the provided URIs.");
    }

    private FetcherResponse fetchResponseFromValidUri(URI uri) throws LookupException {
        try {
            HttpURLConnection connection = getHttpURLConnection(uri);
            return handleResponse(uri, connection);
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

    private static FetcherResponse handleResponse(URI uri, HttpURLConnection connection) throws IOException, LookupException {
        int statusCode = connection.getResponseCode();
        switch (statusCode) {
            case 200:
                return new FetcherResponse(new BufferedInputStream(
                        connection.getInputStream()),
                        connection.getHeaderField("X-SMP-Namespace")
                );

            case 404:
                throw new PeppolResourceException("Participant metadata not found at URI: " + uri);

            case 429:
                throw new PeppolInfrastructureException("Too many requests to SMP server at URI: " + uri + ". Please retry after some time.");


            case 500:
            case 502:
            case 503:
            case 504:
                throw new PeppolInfrastructureException("SMP server error (HTTP " + statusCode + ") while fetching metadata from URI: " + uri);

            default:
                throw new LookupException(String.format("Received HTTP status code %s for lookup at URI: %s", statusCode, uri));
        }
    }


    private HttpURLConnection getHttpURLConnection(URI uri) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) uri.toURL().openConnection();
        if (timeout >= Integer.MIN_VALUE && timeout <= Integer.MAX_VALUE) {
            urlConnection.setConnectTimeout((int) timeout);
            urlConnection.setReadTimeout((int) timeout);
        } else { // set default timeout values if timeout (lookup.fetcher.timeout) is not set in oxalis.conf
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
        }
        return urlConnection;
    }

}
