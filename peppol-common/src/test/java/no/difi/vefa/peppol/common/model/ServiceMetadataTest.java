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

package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class ServiceMetadataTest {

    @Test
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

        ProcessMetadata processMetadata1 = ProcessMetadata.of(ProcessIdentifier.of("Some:Process"), endpoint1, endpoint3);
        ProcessMetadata processMetadata2 = ProcessMetadata.of(ProcessIdentifier.of("Other:Process"), endpoint2);

        ServiceMetadata serviceMetadata = ServiceMetadata.of(
                ParticipantIdentifier.of("9908:991825827"),
                DocumentTypeIdentifier.of("Some:Document"),
                Arrays.asList(processMetadata1, processMetadata2),
                Mockito.mock(X509Certificate.class)
        );

        Assert.assertEquals(serviceMetadata.getParticipantIdentifier(), ParticipantIdentifier.of("9908:991825827"));
        Assert.assertEquals(serviceMetadata.getDocumentTypeIdentifier(), DocumentTypeIdentifier.of("Some:Document"));
        Assert.assertNotNull(serviceMetadata.getSigner());

        Assert.assertEquals(serviceMetadata.getEndpoint(ProcessIdentifier.of("Some:Process"), TransportProfile.AS2_1_0), endpoint1);
        Assert.assertEquals(serviceMetadata.getEndpoint(ProcessIdentifier.of("Some:Process"), TransportProfile.AS2_1_0, TransportProfile.AS4), endpoint1);
        Assert.assertEquals(serviceMetadata.getEndpoint(ProcessIdentifier.of("Some:Process"), TransportProfile.AS4, TransportProfile.AS2_1_0), endpoint3);
        Assert.assertEquals(serviceMetadata.getEndpoint(ProcessIdentifier.of("Other:Process"), TransportProfile.AS2_1_0), endpoint2);

        try {
            serviceMetadata.getEndpoint(ProcessIdentifier.of("Other:Process"), TransportProfile.AS4);
        } catch (EndpointNotFoundException e) {
            // No action.
        }

        try {
            serviceMetadata.getEndpoint(ProcessIdentifier.of("Another:Process"), TransportProfile.AS4, TransportProfile.AS2_1_0, TransportProfile.START);
        } catch (EndpointNotFoundException e) {
            // No action.
        }
    }
}
