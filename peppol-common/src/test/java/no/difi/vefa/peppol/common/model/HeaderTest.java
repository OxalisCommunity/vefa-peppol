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
                .identifier(InstanceIdentifier.of(header.getIdentifier().getIdentifier()));

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

    @Test
    public void shortOfMethod() {
        Header header = Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        );

        Assert.assertTrue(header.equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        )));
        Assert.assertTrue(header.equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document"),
                null, null, null
        )));
        Assert.assertFalse(header.equals(Header.of(
                ParticipantIdentifier.of("9908:98764321"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        )));
        Assert.assertFalse(header.equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456789"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        )));
        Assert.assertFalse(header.equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Other:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        )));
        Assert.assertFalse(header.equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Other:Document")
        )));
        Assert.assertFalse(header.equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        ).instanceType(InstanceType.of("Some", "Type", "1.0"))));
        Assert.assertFalse(header.equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        ).identifier(InstanceIdentifier.generateUUID())));
        Assert.assertFalse(header.equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        ).creationTimestamp(new Date())));
        Assert.assertFalse(header.instanceType(InstanceType.of("Some", "Type", "1.0")).equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        )));
        Assert.assertFalse(header.identifier(InstanceIdentifier.generateUUID()).equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        )));
        Assert.assertFalse(header.creationTimestamp(new Date()).equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        )));
        Assert.assertFalse(header.creationTimestamp(new Date()).equals(Header.of(
                ParticipantIdentifier.of("9908:98764325"),
                ParticipantIdentifier.of("9908123456785"),
                ProcessIdentifier.of("Some:Process"),
                DocumentTypeIdentifier.of("Some:Document")
        )));
        Assert.assertNotNull(header.hashCode());
    }
}
