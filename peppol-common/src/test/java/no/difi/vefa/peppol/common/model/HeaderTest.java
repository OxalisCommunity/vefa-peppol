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

package no.difi.vefa.peppol.common.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class HeaderTest {

    @Test
    public void simple() {
        Header header = Header.newInstance()
                .sender(ParticipantIdentifier.of("9908:987654325"))
                .receiver(ParticipantIdentifier.of("9908:123456785"))
                .process(ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"))
                .documentType(DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice" +
                        "##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"))
                .instanceType(InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "2.0"))
                .creationTimestamp(new Date())
                .identifier(InstanceIdentifier.generateUUID());

        Header header2 = Header.newInstance()
                .sender(ParticipantIdentifier.of("9908:987654325"))
                .receiver(ParticipantIdentifier.of("9908:123456785"))
                .process(ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"))
                .documentType(DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice" +
                        "##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"))
                .instanceType(InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "2.0"))
                .creationTimestamp(header.getCreationTimestamp())
                .identifier(InstanceIdentifier.of(header.getIdentifier().getValue()));

        Assert.assertEquals(header, header2);
        Assert.assertNotNull(header.hashCode());
        Assert.assertNotNull(header.toString());

        Assert.assertTrue(header.equals(header));
        Assert.assertFalse(header.equals("Header"));
        Assert.assertFalse(header.equals(null));

        Assert.assertEquals(header.getSender(), header2.getSender());
        Assert.assertEquals(header.getReceiver(), header2.getReceiver());
        Assert.assertEquals(header.getProcess(), header2.getProcess());
        Assert.assertEquals(header.getDocumentType(), header2.getDocumentType());
        Assert.assertEquals(header.getInstanceType(), header2.getInstanceType());
        Assert.assertEquals(header.getCreationTimestamp(), header2.getCreationTimestamp());
        Assert.assertEquals(header.getIdentifier(), header2.getIdentifier());
    }
}
