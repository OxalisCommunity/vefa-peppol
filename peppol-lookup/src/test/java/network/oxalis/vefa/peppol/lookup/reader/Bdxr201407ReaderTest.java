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

package network.oxalis.vefa.peppol.lookup.reader;

import network.oxalis.vefa.peppol.common.lang.EndpointNotFoundException;
import network.oxalis.vefa.peppol.common.model.*;
import network.oxalis.vefa.peppol.lookup.api.FetcherResponse;
import network.oxalis.vefa.peppol.lookup.api.MetadataReader;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class Bdxr201407ReaderTest {

    private MetadataReader reader = new Bdxr201407Reader();

    @Test
    public void documentIdentifers() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/bdxr201407-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);
    }

    @Test
    public void serviceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/bdxr201407-servicemetadata-9908-810418052.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0");

        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier, TransportProfile.START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(serviceInformation.getEndpoint(processIdentifier, TransportProfile.AS2_1_0));

        assertEquals(serviceInformation.getEndpoint(
                processIdentifier, TransportProfile.AS2_1_0).getCertificate().getSubjectDN().toString(),
                "CN=APP_1000000005, O=DIFI, C=NO"
        );
    }
}
