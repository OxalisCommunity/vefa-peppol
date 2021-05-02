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

public class BusdoxReaderTest {

    private final MetadataReader reader = new BusdoxReader();

    @Test
    public void documentIdentifiers() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicegroup-0192-991825827.xml"), null));

        assertEquals(result.size(), 9);
    }

    @Test
    public void serviceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-0192-991825827.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier, TransportProfile.AS4);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS2_2_0));

        assertEquals(
                serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS4_2_0)
                        .getCertificate().getSubjectDN().toString(),
                "C=NO, O=TietoEVRY, OU=PEPPOL PRODUCTION AP, CN=PNO000051"
        );
    }

    @Test
    public void serviceMetadataRedirect() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-redirect-0192-991825827.xml"))).getContent();

        assertNull(result.getServiceInformation());
        Redirect redirect = result.getRedirect();
        assertNotNull(redirect);

        assertEquals(redirect.getCertificateUID(), "C=NO,O=Direktoratet for Forvaltning og IKT,OU=PEPPOL TEST SMP,CN=PNO000179");
        assertEquals(redirect.getHref(), "http://test-smp2.difi.no/iso6523-actorid-upis%3A%3A0192%3A991825827/services/busdox-docid-qns%3A%3Aurn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AOrderResponse-2%3A%3AOrderResponse%23%23urn%3Afdc%3Apeppol.eu%3Apoacc%3Atrns%3Aorder_response%3A3%3Aextended%3Aurn%3Afdc%3Aanskaffelser.no%3A2019%3Aehf%3Aspec%3A3.0%3A%3A2.2");
    }

    @Test
    public void documentIdentifiersDocsLogistics() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicegroup-docslogistics.xml"), null));

        assertEquals(result.size(), 25);
    }
}
