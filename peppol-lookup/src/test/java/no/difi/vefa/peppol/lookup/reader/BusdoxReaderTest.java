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

package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.ServiceMetadata;
import no.difi.vefa.peppol.common.model.ServiceReference;
import no.difi.vefa.peppol.common.model.TransportProfile;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class BusdoxReaderTest {

    private MetadataReader reader = new BusdoxReader();

    @Test
    public void documentIdentifers() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);
    }

    @Test
    public void serviceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-991825827.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");

        try {
            result.getEndpoint(processIdentifier, TransportProfile.START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(result.getEndpoint(processIdentifier, TransportProfile.AS2_1_0));

        assertEquals(
                result.getEndpoint(processIdentifier, TransportProfile.AS2_1_0)
                        .getCertificate().getSubjectDN().toString(),
                "O=EVRY AS, CN=APP_1000000025, C=NO"
        );
    }

    @Test
    public void documentIdentifiersDocsLogistics() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicegroup-docslogistics.xml"), null));

        assertEquals(result.size(), 25);
    }
}
