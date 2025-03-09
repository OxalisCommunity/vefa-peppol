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

package network.oxalis.vefa.peppol.common.model;

import network.oxalis.vefa.peppol.common.lang.EndpointNotFoundException;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class ServiceMetadataTest {

    @Test
    @SuppressWarnings("deprecation")
    public void simple() throws Exception {
        Endpoint endpoint1 = Endpoint.of(
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class)
        );
        Endpoint endpoint2 = Endpoint.of(
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class)
        );
        Endpoint endpoint3 = Endpoint.of(
                TransportProfile.AS4,
                URI.create("https://ap.example.com/as4"),
                Mockito.mock(X509Certificate.class)
        );

        ProcessMetadata<Endpoint> processMetadata1 = ProcessMetadata.of(
                ProcessIdentifier.of("Some:Process"), endpoint1, endpoint3);
        ProcessMetadata<Endpoint> processMetadata2 = ProcessMetadata.of(
                ProcessIdentifier.of("Other:Process"), endpoint2);

        ServiceMetadata serviceMetadata = ServiceMetadata.of(ServiceInformation.of(
                ParticipantIdentifier.of("9908:991825827"),
                DocumentTypeIdentifier.of("Some:Document"),
                Arrays.asList(processMetadata1, processMetadata2)
        ));

        ServiceInformation<Endpoint> serviceInformation = serviceMetadata.getServiceInformation();

        Assert.assertEquals(serviceInformation.getProcesses().size(), 2);

        Assert.assertEquals(serviceInformation.getParticipantIdentifier(), ParticipantIdentifier.of("9908:991825827"));
        Assert.assertEquals(serviceInformation.getDocumentTypeIdentifier(), DocumentTypeIdentifier.of("Some:Document"));

        Assert.assertEquals(serviceInformation.getEndpoint(ProcessIdentifier.of("Some:Process"),
                TransportProfile.AS2_1_0),
                endpoint1);
        Assert.assertEquals(serviceInformation.getEndpoint(ProcessIdentifier.of("Some:Process"),
                TransportProfile.AS2_1_0, TransportProfile.AS4),
                endpoint1);
        Assert.assertEquals(serviceInformation.getEndpoint(ProcessIdentifier.of("Some:Process"),
                TransportProfile.AS4, TransportProfile.AS2_1_0),
                endpoint3);
        Assert.assertEquals(serviceInformation.getEndpoint(ProcessIdentifier.of("Other:Process"),
                TransportProfile.AS2_1_0),
                endpoint2);

        try {
            serviceInformation.getEndpoint(ProcessIdentifier.of("Other:Process"), TransportProfile.AS4);
            Assert.fail();
        } catch (EndpointNotFoundException e) {
            // No action.
        }

        try {
            serviceInformation.getEndpoint(ProcessIdentifier.of("Another:Process"),
                    TransportProfile.AS4, TransportProfile.AS2_1_0, TransportProfile.START);
            Assert.fail();
        } catch (EndpointNotFoundException e) {
            // No action.
        }
    }

    @Test
    public void redirect() {
        ServiceMetadata serviceMetadata = ServiceMetadata.of(Redirect.of(
                "C=NO,O=Direktoratet for Forvaltning og IKT,OU=PEPPOL TEST SMP,CN=PNO000179",
                "http://test-smp.difi.no/iso6523-actorid-upis%3A%3A0192%3A991825827/services/busdox-docid-qns%3A%3Aurn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AOrderResponse-2%3A%3AOrderResponse%23%23urn%3Afdc%3Apeppol.eu%3Apoacc%3Atrns%3Aorder_response%3A3%3Aextended%3Aurn%3Afdc%3Aanskaffelser.no%3A2019%3Aehf%3Aspec%3A3.0%3A%3A2.2"
        ));

        Redirect redirect = serviceMetadata.getRedirect();

        assertEquals(redirect.getCertificateUID(), "C=NO,O=Direktoratet for Forvaltning og IKT,OU=PEPPOL TEST SMP,CN=PNO000179");
        assertEquals(redirect.getHref(), "http://test-smp.difi.no/iso6523-actorid-upis%3A%3A0192%3A991825827/services/busdox-docid-qns%3A%3Aurn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AOrderResponse-2%3A%3AOrderResponse%23%23urn%3Afdc%3Apeppol.eu%3Apoacc%3Atrns%3Aorder_response%3A3%3Aextended%3Aurn%3Afdc%3Aanskaffelser.no%3A2019%3Aehf%3Aspec%3A3.0%3A%3A2.2");
    }
}
