/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
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

package no.difi.vefa.peppol.lookup;

import no.difi.vefa.peppol.lookup.api.MetadataFetcher;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import no.difi.vefa.peppol.lookup.api.MetadataProvider;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class LookupClientBuilderTest {

    @Test
    public void success() throws Exception {
        assertNotNull(LookupClientBuilder.forProduction());
        assertNotNull(LookupClientBuilder.forTest());
    }

    @Test
    public void testMissingLocator() throws Exception {
        assertNotNull(LookupClientBuilder.forProduction().locator((MetadataLocator) null).build());
    }

    @Test
    public void testMissingProvider() throws Exception {
        assertNotNull(LookupClientBuilder.forProduction().provider((MetadataProvider) null).build());
    }

    @Test
    public void testMissingFetcher() throws Exception {
        assertNotNull(LookupClientBuilder.forProduction().fetcher((MetadataFetcher) null).build());
    }

    @Test
    public void testMissingReader() throws Exception {
        assertNotNull(LookupClientBuilder.forProduction().reader((MetadataReader) null).build());
    }
}
