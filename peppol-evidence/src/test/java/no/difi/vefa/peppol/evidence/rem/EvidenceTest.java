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

package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.common.code.DigestMethod;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.evidence.jaxb.receipt.TransmissionRole;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class EvidenceTest {

    public static final Evidence EVIDENCE = Evidence.newInstance()
            .type(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT)
            .eventCode(EventCode.ACCEPTANCE)
            .eventReason(EventReason.OTHER)
            .issuer("Java Testing")
            .evidenceIdentifier(InstanceIdentifier.generateUUID())
            .issuerPolicy("Some Policy")
            .timestamp(new Date())
            .sender(ParticipantIdentifier.of("9908:123456785"))
            .receiver(ParticipantIdentifier.of("9908:987654325"))
            .documentTypeIdentifier(DocumentTypeIdentifier.of(
                    "urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender" +
                            "##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0" +
                            "::2.1", Scheme.NONE))
            .documentIdentifier(InstanceIdentifier.generateUUID())
            .issuerPolicy("Some Policy")
            .messageIdentifier(InstanceIdentifier.generateUUID())
            .digest(Digest.of(DigestMethod.SHA256, "VGhpc0lzQVNIQTI1NkRpZ2VzdA==".getBytes()))
            .transportProtocol(TransportProtocol.AS2)
            .transmissionRole(TransmissionRole.C_3)
            .originalReceipt(Receipt.of("text/plain", "Hello World!".getBytes()));

    @Test
    public void simpleToString() {
        Assert.assertTrue(EVIDENCE.toString().contains("Tender-2"));
    }

    @Test
    public void allowAddingNullReceipt() {
        Evidence evidence = Evidence.newInstance();
        Assert.assertEquals(evidence.getOriginalReceipts().size(), 0);

        evidence = evidence.originalReceipt(null);
        Assert.assertEquals(evidence.getOriginalReceipts().size(), 0);

        evidence = evidence.originalReceipt(Receipt.of("Hello World!".getBytes()));
        Assert.assertEquals(evidence.getOriginalReceipts().size(), 1);
    }

    @Test
    public void simpleHasPeppolExtensionValues() {
        Assert.assertFalse(Evidence.newInstance()
                .hasPeppolExtensionValues());

        Assert.assertTrue(Evidence.newInstance()
                .transmissionRole(TransmissionRole.C_2)
                .hasPeppolExtensionValues());

        Assert.assertTrue(Evidence.newInstance()
                .transportProtocol(TransportProtocol.INTERNAL)
                .hasPeppolExtensionValues());

        Assert.assertTrue(Evidence.newInstance()
                .originalReceipt(Receipt.of("Hello World!".getBytes()))
                .hasPeppolExtensionValues());

        Assert.assertTrue(EVIDENCE.hasPeppolExtensionValues());
    }

    @Test
    public void simpleHashCode() {
        Assert.assertNotNull(EVIDENCE.hashCode());
        Assert.assertNotNull(EVIDENCE.type(null).hashCode());
        Assert.assertNotNull(EVIDENCE.eventCode(null).hashCode());
        Assert.assertNotNull(EVIDENCE.eventReason(null).hashCode());
        Assert.assertNotNull(EVIDENCE.issuer(null).hashCode());
        Assert.assertNotNull(EVIDENCE.evidenceIdentifier(null).hashCode());
        Assert.assertNotNull(EVIDENCE.timestamp(null).hashCode());
        Assert.assertNotNull(EVIDENCE.sender(null).hashCode());
        Assert.assertNotNull(EVIDENCE.receiver(null).hashCode());
        Assert.assertNotNull(EVIDENCE.documentTypeIdentifier(null).hashCode());
        Assert.assertNotNull(EVIDENCE.documentIdentifier(null).hashCode());
        Assert.assertNotNull(EVIDENCE.issuerPolicy(null).hashCode());
        Assert.assertNotNull(EVIDENCE.digest(null).hashCode());
        Assert.assertNotNull(EVIDENCE.messageIdentifier(null).hashCode());
        Assert.assertNotNull(EVIDENCE.transportProtocol(null).hashCode());
        Assert.assertNotNull(EVIDENCE.transmissionRole(null).hashCode());
    }

    @Test
    public void simpleEquals() {
        Assert.assertTrue(EVIDENCE.equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.equals(null));
        Assert.assertFalse(EVIDENCE.equals("Test"));

        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.type(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.eventCode(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.eventReason(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.issuer(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.evidenceIdentifier(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.timestamp(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.sender(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.receiver(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.documentTypeIdentifier(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.documentIdentifier(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.issuerPolicy(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.digest(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.messageIdentifier(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.transportProtocol(null)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.transmissionRole(null)));

        Assert.assertFalse(EVIDENCE.type(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.eventCode(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.eventReason(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.issuer(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.evidenceIdentifier(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.timestamp(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.sender(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.receiver(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.documentTypeIdentifier(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.documentIdentifier(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.issuerPolicy(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.digest(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.messageIdentifier(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.transportProtocol(null).equals(EVIDENCE));
        Assert.assertFalse(EVIDENCE.transmissionRole(null).equals(EVIDENCE));

        Assert.assertTrue(EVIDENCE.type(null).equals(EVIDENCE.type(null)));
        Assert.assertTrue(EVIDENCE.eventCode(null).equals(EVIDENCE.eventCode(null)));
        Assert.assertTrue(EVIDENCE.eventReason(null).equals(EVIDENCE.eventReason(null)));
        Assert.assertTrue(EVIDENCE.issuer(null).equals(EVIDENCE.issuer(null)));
        Assert.assertTrue(EVIDENCE.evidenceIdentifier(null).equals(EVIDENCE.evidenceIdentifier(null)));
        Assert.assertTrue(EVIDENCE.timestamp(null).equals(EVIDENCE.timestamp(null)));
        Assert.assertTrue(EVIDENCE.sender(null).equals(EVIDENCE.sender(null)));
        Assert.assertTrue(EVIDENCE.receiver(null).equals(EVIDENCE.receiver(null)));
        Assert.assertTrue(EVIDENCE.documentTypeIdentifier(null).equals(EVIDENCE.documentTypeIdentifier(null)));
        Assert.assertTrue(EVIDENCE.documentIdentifier(null).equals(EVIDENCE.documentIdentifier(null)));
        Assert.assertTrue(EVIDENCE.issuerPolicy(null).equals(EVIDENCE.issuerPolicy(null)));
        Assert.assertTrue(EVIDENCE.digest(null).equals(EVIDENCE.digest(null)));
        Assert.assertTrue(EVIDENCE.messageIdentifier(null).equals(EVIDENCE.messageIdentifier(null)));
        Assert.assertTrue(EVIDENCE.transportProtocol(null).equals(EVIDENCE.transportProtocol(null)));
        Assert.assertTrue(EVIDENCE.transmissionRole(null).equals(EVIDENCE.transmissionRole(null)));

        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.type(EvidenceTypeInstance.RELAY_REM_MD_ACCEPTANCE_REJECTION)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.eventCode(EventCode.REJECTION)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.eventReason(EventReason.MAILBOX_FULL)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.issuer("Somebody")));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.evidenceIdentifier(InstanceIdentifier.generateUUID())));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.timestamp(new Date(System.currentTimeMillis() + (10 * 1000)))));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.sender(ParticipantIdentifier.of("9908:999999999"))));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.receiver(ParticipantIdentifier.of("9908:111111111"))));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.documentTypeIdentifier(DocumentTypeIdentifier.of("Testing..."))));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.documentIdentifier(InstanceIdentifier.generateUUID())));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.issuerPolicy("Other policy.")));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.digest(Digest.of(DigestMethod.SHA1, "test".getBytes()))));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.messageIdentifier(InstanceIdentifier.generateUUID())));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.transportProtocol(TransportProtocol.INTERNAL)));
        Assert.assertFalse(EVIDENCE.equals(EVIDENCE.transmissionRole(TransmissionRole.C_2)));
    }

    @Test
    public void simpleHeader() {
        ParticipantIdentifier sender = ParticipantIdentifier.of("9908:999999999");
        ParticipantIdentifier receiver = ParticipantIdentifier.of("9908:111111111");
        DocumentTypeIdentifier documentTypeIdentifier = DocumentTypeIdentifier.of("Testing...");
        InstanceIdentifier documentIdentifier = InstanceIdentifier.generateUUID();

        Evidence evidence = Evidence.newInstance()
                .header(Header.newInstance()
                        .sender(sender)
                        .receiver(receiver)
                        .documentType(documentTypeIdentifier)
                        .identifier(documentIdentifier));

        Assert.assertEquals(evidence.getSender(), sender);
        Assert.assertEquals(evidence.getReceiver(), receiver);
        Assert.assertEquals(evidence.getDocumentIdentifier(), documentIdentifier);
        Assert.assertEquals(evidence.getDocumentTypeIdentifier(), documentTypeIdentifier);
    }
}
