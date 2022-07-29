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

import static network.oxalis.vefa.peppol.common.model.TransportProfile.AS2_1_0;
import static network.oxalis.vefa.peppol.common.model.TransportProfile.START;
import static org.testng.Assert.*;

public class MultiReaderTest {

    private final MetadataReader reader = new MultiReader();

    @Test
    public void busdoxDocumentIdentifers() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicegroup-0192-991825827.xml"), null));

        assertEquals(result.size(), 9);
    }

    @Test
    public void bdxr201407DocumentIdentifers() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/bdxr201407-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);
    }

    @Test
    public void bdxr201605DocumentIdentifers() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/bdxr201605-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);
    }

    @Test
    public void busdoxServiceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-923829644.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier, TransportProfile.AS4);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertEquals(
                serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS4_2_0)
                        .getCertificate().getSubjectDN().toString(),
                "C=FI, O=Basware, OU=PEPPOL TEST AP, CN=POP000010"
        );
    }

    @Test
    public void rejectNoLongerActiveEndpoints() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-923829644.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS2_1_0);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }
    }

    @Test
    public void processActiveEndpoints() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-923829644.xml"))).getContent();


        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        assertNotNull(serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS4_2_0));
    }

    @Test
    public void rejectNotYetActiveEndpoints() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-923829644.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS2_2_0);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }
    }

    @Test
    public void missingBothServiceActivationDateAndServiceExpirationDate() throws Exception {
        // Still valid : CANNOT reject messages if both ServiceActivationDate and ServiceExpirationDate values are missing
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-12345678.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        assertNotNull(serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS4_2_0));
    }

    @Test
    public void missingServiceActivationDateButServiceExpirationDateIsValid() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-12345678.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        assertNotNull(serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS2_1_0));
    }

    @Test
    public void missingServiceExpirationDateButServiceActivationDateIsValid() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-12345678.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        assertNotNull(serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS2_2_0));
    }

    @Test
    public void missingServiceActivationDateAndInvalidServiceExpirationDate() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-98765432.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS2_1_0);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }
    }

    @Test
    public void missingServiceExpirationDateAndInvalidServiceActivationDate() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-98765432.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS2_2_0);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }
    }

    @Test
    public void inValidServiceExpirationDateAndServiceActivationDate() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-98765432.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier, TransportProfile.PEPPOL_AS4_2_0);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }
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
    public void busdoxServiceMetadataMultiProcess() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9933-061828591.xml"))).getContent();

        ProcessIdentifier processIdentifier1 = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0");
        ProcessIdentifier processIdentifier2 = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii46:ver1.0");

        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier1, START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(serviceInformation.getEndpoint(processIdentifier1, AS2_1_0));

        assertEquals(
                serviceInformation.getEndpoint(processIdentifier1, AS2_1_0).getCertificate().getSubjectDN().toString(),
                "O=University of Piraeus Research Center, CN=APP_1000000088, C=GR"
        );

        try {
            serviceInformation.getEndpoint(processIdentifier2, START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(serviceInformation.getEndpoint(processIdentifier2, AS2_1_0));

        assertEquals(
                serviceInformation.getEndpoint(processIdentifier2, AS2_1_0).getCertificate().getSubjectDN().toString(),
                "O=University of Piraeus Research Center, CN=APP_1000000088, C=GR"
        );
    }

    @Test
    public void bdxrServiceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(
                new FetcherResponse(getClass().getResourceAsStream("/bdxr201407-servicemetadata-9908-810418052.xml")))
                .getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0");
        ServiceInformation<Endpoint> serviceInformation = result.getServiceInformation();

        try {
            serviceInformation.getEndpoint(processIdentifier, START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(serviceInformation.getEndpoint(processIdentifier, AS2_1_0));

        assertEquals(
                serviceInformation.getEndpoint(processIdentifier, AS2_1_0).getCertificate().getSubjectDN().toString(),
                "CN=APP_1000000005, O=DIFI, C=NO"
        );
    }

    @Test
    public void busdoxServiceGroup() throws Exception {
        List<ServiceReference> serviceReferences = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicegroup-9915-setcce-test.xml"), null));
        assertEquals(serviceReferences.size(), 1);
    }
}
