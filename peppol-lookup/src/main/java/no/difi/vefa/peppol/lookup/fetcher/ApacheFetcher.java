/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.lookup.fetcher;

import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.LookupException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;

public class ApacheFetcher extends AbstractFetcher {

    private static Logger logger = LoggerFactory.getLogger(ApacheFetcher.class);

    private HttpClient httpClient;

    private RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(TIMEOUT)
            .setConnectTimeout(TIMEOUT)
            .setSocketTimeout(TIMEOUT)
            .build();

    public ApacheFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ApacheFetcher() {
        this(HttpClients.createDefault());
    }

    @Override
    public FetcherResponse fetch(URI uri) throws LookupException {
        try {
            logger.debug("{}", uri);

            HttpGet httpGet = new HttpGet(uri);
            httpGet.setConfig(requestConfig);

            HttpResponse response = httpClient.execute(httpGet);

            switch (response.getStatusLine().getStatusCode()) {
                case 200:
                    return new FetcherResponse(
                            new BufferedInputStream(response.getEntity().getContent()),
                            response.containsHeader("X-SMP-Namespace") ?
                                    response.getFirstHeader("X-SMP-Namespace").getValue() : null
                    );
                case 404:
                    throw new LookupException("Not supported.");
                default:
                    throw new LookupException(
                            String.format("Received code %s for lookup.", response.getStatusLine().getStatusCode()));
            }
        } catch (SocketTimeoutException | SocketException e) {
            throw new LookupException(String.format("Unable to fetch '%s'", uri), e);
        } catch (Exception e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
