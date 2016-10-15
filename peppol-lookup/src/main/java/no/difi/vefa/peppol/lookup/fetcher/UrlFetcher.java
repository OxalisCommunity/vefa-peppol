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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLConnection;

public class UrlFetcher extends AbstractFetcher {

    @Override
    public FetcherResponse fetch(URI uri) throws LookupException {
        try {
            URLConnection urlConnection = uri.toURL().openConnection();
            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setReadTimeout(TIMEOUT);

            return new FetcherResponse(
                    new BufferedInputStream(urlConnection.getInputStream()),
                    urlConnection.getHeaderField("X-SMP-Namespace"));
        } catch (FileNotFoundException e) {
            throw new LookupException("Invalid response from SMP.", e);
        } catch (SocketTimeoutException | SocketException e) {
            throw new LookupException(String.format("Unable to fetch '%s'", uri), e);
        } catch (IOException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
