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
import network.oxalis.vefa.peppol.lookup.api.FetcherResponse;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.mode.Mode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

public class ApacheFetcher extends BasicApacheFetcher {

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

    public FetcherResponse fetch(List<URI> uriList) throws LookupException, FileNotFoundException {
        FetcherResponse fetcherResponse = null;
        Exception exceptionObj = null;

        if (uriList == null || uriList.isEmpty()) {
            throw new LookupException("Unable to lookup requested URL or SMP registration is not valid.");
        }

        for (URI uri : uriList) {
            try {
                fetcherResponse = fetchResponseFromValidUri(uri);
                if (fetcherResponse != null) {
                    exceptionObj = null;
                    break;
                }
            } catch (FileNotFoundException | LookupException e) {
                exceptionObj = e;
            }
        }

        if (exceptionObj instanceof FileNotFoundException) {
            throw new FileNotFoundException();
        }

        if (exceptionObj instanceof LookupException) {
            throw new LookupException(exceptionObj.getMessage(), exceptionObj);
        }

        return fetcherResponse;
    }

    private FetcherResponse fetchResponseFromValidUri(URI uri) throws LookupException, FileNotFoundException {

        try (CloseableHttpClient httpClient = createClient()) {

            HttpGet httpGet = new HttpGet(uri);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                switch (response.getStatusLine().getStatusCode()) {
                    case 200:
                        return new FetcherResponse(
                                new ByteArrayInputStream(ByteStreams.toByteArray(response.getEntity().getContent())),
                                // new BufferedInputStream(response.getEntity().getContent()),
                                response.containsHeader("X-SMP-Namespace") ?
                                        response.getFirstHeader("X-SMP-Namespace").getValue() : null
                        );
                    case 404:
                        throw new FileNotFoundException(uri.toString());
                    default:
                        throw new LookupException(String.format(
                                "Received code %s for lookup. URI: %s", response.getStatusLine().getStatusCode(), uri));
                }
            }

        } catch (SocketTimeoutException | SocketException | UnknownHostException e) {
            throw new LookupException(String.format("Unable to fetch '%s'", uri), e);
        } catch (LookupException | FileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
