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

import network.oxalis.vefa.peppol.lookup.api.FetcherResponse;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.mode.Mode;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.List;

public class UrlFetcher extends AbstractFetcher {

    public UrlFetcher(Mode mode) {
        super(mode);
    }

    @Override
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
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) uri.toURL().openConnection();
            if (timeout >= Integer.MIN_VALUE && timeout <= Integer.MAX_VALUE) {
                urlConnection.setConnectTimeout((int) timeout);
                urlConnection.setReadTimeout((int) timeout);
            } else { // set default timeout values if timeout (lookup.fetcher.timeout) is not set in oxalis.conf
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
            }

            if (urlConnection.getResponseCode() != 200) {
                return null;
            }

            return new FetcherResponse(
                    new BufferedInputStream(urlConnection.getInputStream()),
                    urlConnection.getHeaderField("X-SMP-Namespace"));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(uri.toString());
        } catch (SocketTimeoutException | SocketException e) {
            throw new LookupException(String.format("Unable to fetch '%s'", uri), e);
        } catch (IOException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }

}
