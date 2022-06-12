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

import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.MetadataFetcher;
import network.oxalis.vefa.peppol.mode.Mode;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ApacheFetcherTest {

    private MetadataFetcher fetcher = new ApacheFetcher(Mode.of("TEST"));

    @Test(expectedExceptions = LookupException.class)
    public void simpleTimeout() throws LookupException, FileNotFoundException {
        List<URI> uriList = new ArrayList<URI>();
        uriList.add(URI.create("http://invalid.example.com/"));
        fetcher.fetch(uriList);
    }

    @Test(expectedExceptions = FileNotFoundException.class)
    public void simple404() throws LookupException, FileNotFoundException {
        List<URI> uriList = new ArrayList<URI>();
        uriList.add(URI.create("http://httpstat.us/404"));
        fetcher.fetch(uriList);
    }

    @Test(expectedExceptions = LookupException.class)
    public void simple500() throws LookupException, FileNotFoundException {
        List<URI> uriList = new ArrayList<URI>();
        uriList.add(URI.create("http://httpstat.us/500"));
        fetcher.fetch(uriList);
    }

    @Test(expectedExceptions = LookupException.class)
    public void simpleNullPointer() throws LookupException, FileNotFoundException {
        fetcher.fetch(null);
    }
}
