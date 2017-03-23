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
