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

        ProcessMetadata<Endpoint> processMetadata1 = ProcessMetadata.of(
                ProcessIdentifier.of("Some:Process"), endpoint1, endpoint3);
        ProcessMetadata<Endpoint> processMetadata2 = ProcessMetadata.of(
                ProcessIdentifier.of("Other:Process"), endpoint2);

        ServiceMetadata serviceMetadata = ServiceMetadata.of(
                ParticipantIdentifier.of("9908:991825827"),
                DocumentTypeIdentifier.of("Some:Document"),
                Arrays.asList(processMetadata1, processMetadata2)
        );

        Assert.assertEquals(serviceMetadata.getProcesses().size(), 2);

        Assert.assertEquals(serviceMetadata.getParticipantIdentifier(), ParticipantIdentifier.of("9908:991825827"));
        Assert.assertEquals(serviceMetadata.getDocumentTypeIdentifier(), DocumentTypeIdentifier.of("Some:Document"));

        Assert.assertEquals(serviceMetadata.getEndpoint(ProcessIdentifier.of("Some:Process"),
                TransportProfile.AS2_1_0),
                endpoint1);
        Assert.assertEquals(serviceMetadata.getEndpoint(ProcessIdentifier.of("Some:Process"),
                TransportProfile.AS2_1_0, TransportProfile.AS4),
                endpoint1);
        Assert.assertEquals(serviceMetadata.getEndpoint(ProcessIdentifier.of("Some:Process"),
                TransportProfile.AS4, TransportProfile.AS2_1_0),
                endpoint3);
        Assert.assertEquals(serviceMetadata.getEndpoint(ProcessIdentifier.of("Other:Process"),
                TransportProfile.AS2_1_0),
                endpoint2);

        try {
            serviceMetadata.getEndpoint(ProcessIdentifier.of("Other:Process"), TransportProfile.AS4);
            Assert.fail();
        } catch (EndpointNotFoundException e) {
            // No action.
        }

        try {
            serviceMetadata.getEndpoint(ProcessIdentifier.of("Another:Process"),
                    TransportProfile.AS4, TransportProfile.AS2_1_0, TransportProfile.START);
            Assert.fail();
        } catch (EndpointNotFoundException e) {
            // No action.
        }
    }
}
