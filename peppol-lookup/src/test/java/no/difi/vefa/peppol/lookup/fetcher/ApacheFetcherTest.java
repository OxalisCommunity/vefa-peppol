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

package no.difi.vefa.peppol.lookup.fetcher;

import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataFetcher;
import no.difi.vefa.peppol.mode.Mode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.net.URI;

public class ApacheFetcherTest {

    private MetadataFetcher fetcher = new ApacheFetcher(Mode.of("TEST"));

    @Test(expectedExceptions = LookupException.class)
    public void simpleTimeout() throws LookupException, FileNotFoundException {
        fetcher.fetch(URI.create("http://invalid.example.com/"));
    }

    @Test(expectedExceptions = FileNotFoundException.class)
    public void simple404() throws LookupException, FileNotFoundException {
        fetcher.fetch(URI.create("http://httpstat.us/404"));
    }

    @Test(expectedExceptions = LookupException.class)
    public void simple500() throws LookupException, FileNotFoundException {
        fetcher.fetch(URI.create("http://httpstat.us/500"));
    }

    @Test(expectedExceptions = LookupException.class)
    public void simpleNullPointer() throws LookupException, FileNotFoundException {
        fetcher.fetch(null);
    }
}
