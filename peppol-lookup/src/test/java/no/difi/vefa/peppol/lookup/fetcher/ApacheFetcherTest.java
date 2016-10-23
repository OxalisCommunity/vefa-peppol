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

import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataFetcher;
import org.testng.annotations.Test;

import java.net.URI;

public class ApacheFetcherTest {

    @Test(expectedExceptions = LookupException.class)
    public void simpleTimeout() throws LookupException {
        MetadataFetcher fetcher = new ApacheFetcher();

        fetcher.fetch(URI.create("http://invalid.example.com/"));
    }

    @Test(expectedExceptions = LookupException.class)
    public void simple404() throws LookupException {
        MetadataFetcher fetcher = new ApacheFetcher();

        fetcher.fetch(URI.create("http://httpstat.us/404"));
    }

    @Test(expectedExceptions = LookupException.class)
    public void simple500() throws LookupException {
        MetadataFetcher fetcher = new ApacheFetcher();

        fetcher.fetch(URI.create("http://httpstat.us/500"));
    }

    @Test(expectedExceptions = LookupException.class)
    public void simpleNullPointer() throws LookupException {
        MetadataFetcher fetcher = new ApacheFetcher();

        fetcher.fetch(null);
    }
}
