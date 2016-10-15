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

package no.difi.vefa.peppol.lookup;

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public class LookupClientBuilderTest {

    @Test
    public void success() {
        assertNotNull(LookupClientBuilder.forProduction());
        assertNotNull(LookupClientBuilder.forTest());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingLocator() {
        LookupClientBuilder.forProduction().locator(null).build();
        fail("Exception expected");
    }

    @Test
    public void testMissingProvider() {
        assertNotNull(LookupClientBuilder.forProduction().provider(null).build());
    }

    @Test
    public void testMissingFetcher() {
        assertNotNull(LookupClientBuilder.forProduction().fetcher(null).build());
    }

    @Test
    public void testMissingReader() {
        assertNotNull(LookupClientBuilder.forProduction().reader(null).build());
    }
}
