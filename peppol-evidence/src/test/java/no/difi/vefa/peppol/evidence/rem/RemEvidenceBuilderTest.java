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

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.TransportProtocol;
import no.difi.vefa.peppol.evidence.jaxb.receipt.PeppolRemExtension;
import no.difi.vefa.peppol.evidence.jaxb.receipt.TransmissionRole;
import no.difi.vefa.peppol.evidence.jaxb.rem.ExtensionType;
import no.difi.vefa.peppol.evidence.jaxb.rem.ExtensionsListType;
import no.difi.vefa.peppol.evidence.jaxb.rem.REMEvidenceType;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.util.Date;

import static org.testng.Assert.*;

/**
 * Ensures that the RemEvidenceBuilder works as expected.
 *
 * @author steinar
 *         Date: 04.11.2015
 *         Time: 21.05
 * @author Sander Fieten
 *         Date: 06.04.2016
 */
public class RemEvidenceBuilderTest {

    protected byte[] specificReceiptBytes;

    protected KeyStore.PrivateKeyEntry privateKeyEntry;

    protected RemEvidenceService remEvidenceService;

    @BeforeClass
    public void setUp() {
        // Provides sample AS2 MDN to be included as evidence in the REM
        specificReceiptBytes = TestResources.getSampleMdnSmime();

        // Grabs our private key and certificate to be used for signing the REM
        privateKeyEntry = TestResources.getPrivateKey();

        // Allows us the obtain the JAXBContext, which is needed when creating instances of RemEvidenceBuilder
        // RemEvidenceBuilder instances can only be created by using the factory, but since this is a unit test,
        // we are allowed to go behind the scenes.
        remEvidenceService = new RemEvidenceService();
    }

    @Test
    public void createSampleRemEvidence() throws Exception {

        RemEvidenceBuilder builder = remEvidenceService.createDeliveryNonDeliveryToRecipientBuilder();
        builder.eventCode(EventCode.ACCEPTANCE)
                .eventTime(new Date())
                .eventReason(EventReason.OTHER)
                .evidenceIssuerPolicyID(TestResources.EVIDENCE_ISSUER_POLICY_ID)
                .evidenceIssuerDetails(TestResources.EVIDENCE_ISSUER_NAME)
                .senderIdentifier(TestResources.SENDER_IDENTIFIER)
                .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
                .documentTypeId(TestResources.DOC_TYPE_ID)
                .documentTypeInstanceIdentifier(TestResources.DOC_TYPE_INSTANCE_ID)
                .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
                .payloadDigest("ThisIsASHA256Digest".getBytes())
                .protocolSpecificEvidence(TransmissionRole.C_3, TransportProtocol.AS2, specificReceiptBytes)
        ;


        // Signs and builds the REMEvidenceType instance
        SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKeyEntry);


        // Grabs the REMEvidenceType instance in order to make some assertions.
        REMEvidenceType remEvidenceInstance = signedRemEvidence.getRemEvidenceType();

        // Issue #2
        assertNotNull(remEvidenceInstance.getVersion(), "The version attribute was not set!");
        assertEquals(remEvidenceInstance.getVersion(), "2");
        // ------------- Issue #2 --------------


        assertEquals(remEvidenceInstance.getEventCode(), EventCode.ACCEPTANCE.getValue().toString());

        // Transforms the rem evidence instance into an XML representation suitable for some checks.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new RemEvidenceTransformer().toFormattedXml(signedRemEvidence, baos);
        String xmlOutput = baos.toString("UTF-8");

        System.out.println(xmlOutput);

        assertTrue(xmlOutput.contains(TestResources.DOC_TYPE_ID.getIdentifier()),
                "Document type id has not been included in the REM XML");
        assertTrue(xmlOutput.contains(TestResources.INSTANCE_IDENTIFIER.getIdentifier()),
                "Instance identifier missing");
        assertTrue(xmlOutput.contains(TestResources.SENDER_IDENTIFIER.getIdentifier()),
                "Sender identifier missing in generated xml");
        assertTrue(xmlOutput.contains(TestResources.RECIPIENT_IDENTIFIER.getIdentifier()),
                "Recipient identifier missing in generated xml");

        // Verifies that the signature was created, note that we omit the opening '<' as we have no idea
        // what the namespace might be.
        // If we were a little more eager we would use XPath to verify the contents :-)

        assertTrue(xmlOutput.contains("SignatureValue>"));
        assertTrue(xmlOutput.contains("KeyInfo>"));

        // Verifies the signature using the W3C Document
        XmldsigVerifier.verify(signedRemEvidence.getDocument());

        assertNotNull(signedRemEvidence.getEventCode());
        assertNotNull(signedRemEvidence.getEventReason());
        assertNotNull(signedRemEvidence.getEventTime());

        // Check the policy id
        assertEquals(signedRemEvidence.getEvidenceIssuerPolicyID(), TestResources.EVIDENCE_ISSUER_POLICY_ID);

        // Check entity name of evidence issuer (issue #11)
        assertEquals(signedRemEvidence.getEvidenceIssuerDetails(), TestResources.EVIDENCE_ISSUER_NAME);

        ParticipantIdentifier senderIdentifier = signedRemEvidence.getSenderIdentifier();
        assertNotNull(senderIdentifier);

        ParticipantIdentifier receiverIdentifier = signedRemEvidence.getRecipientIdentifier();
        assertNotNull(receiverIdentifier);

        DocumentTypeIdentifier documentTypeIdentifier = signedRemEvidence.getDocumentTypeIdentifier();
        assertNotNull(documentTypeIdentifier);

        String documentTypeInstanceId = signedRemEvidence.getDocumentTypeInstanceIdentifier();
        assertEquals(documentTypeInstanceId, TestResources.DOC_TYPE_INSTANCE_ID);

        byte[] digestBytes = signedRemEvidence.getPayloadDigestValue();
        assertNotNull(digestBytes);
        assertTrue(digestBytes.length > 0);
        PeppolRemExtension transmissionEvidence = signedRemEvidence.getTransmissionEvidence();
        assertNotNull(transmissionEvidence);

        assertEquals(transmissionEvidence.getTransmissionProtocol(), TransportProtocol.AS2.getIdentifier());
        assertEquals(transmissionEvidence.getTransmissionRole(), TransmissionRole.C_3);


    }


    /**
     * Verifies that the PeppolRemExtension is unmarshalled correctly.
     *
     * @throws Exception
     */
    @Test
    public void testUnmarshal() throws Exception {

        RemEvidenceBuilder builder = new RemEvidenceBuilder(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT);

        builder.eventCode(EventCode.ACCEPTANCE)
                .eventTime(new Date())
                .eventReason(EventReason.OTHER)
                .evidenceIssuerPolicyID(TestResources.EVIDENCE_ISSUER_POLICY_ID)
                .evidenceIssuerDetails(TestResources.EVIDENCE_ISSUER_NAME)
                .senderIdentifier(TestResources.SENDER_IDENTIFIER)
                .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
                .documentTypeId(TestResources.DOC_TYPE_ID)
                .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
                .payloadDigest("ThisIsASHA256Digest".getBytes())
                .protocolSpecificEvidence(TransmissionRole.C_3, TransportProtocol.AS2, specificReceiptBytes);


        // Signs and builds the REMEvidenceType instance
        SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKeyEntry);

        // Grabs the REMEvidenceType instance in order to make some assertions.
        REMEvidenceType remEvidenceInstance = signedRemEvidence.getRemEvidenceType();
        assertNotNull(remEvidenceInstance);


        ExtensionType extensionType = signedRemEvidence.getRemEvidenceType().getExtensions().getExtension().get(0);
        Object value = extensionType.getContent().get(0);

        assertTrue(value instanceof PeppolRemExtension);

        PeppolRemExtension peppolRemExtension = (PeppolRemExtension) value;
        byte[] evidenceBytes = peppolRemExtension.getOriginalReceipt().get(0).getValue();

        assertEquals(evidenceBytes, specificReceiptBytes);
    }

    /**
     * Verifies that the PeppolRemExtension is optional.
     *
     * @throws Exception
     */
    @Test
    public void testExtensionOptional() throws Exception {

        RemEvidenceBuilder builder = new RemEvidenceBuilder(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT);

        builder.eventCode(EventCode.ACCEPTANCE)
                .eventTime(new Date())
                .eventReason(EventReason.OTHER)
                .evidenceIssuerPolicyID(TestResources.EVIDENCE_ISSUER_POLICY_ID)
                .evidenceIssuerDetails(TestResources.EVIDENCE_ISSUER_NAME)
                .senderIdentifier(TestResources.SENDER_IDENTIFIER)
                .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
                .documentTypeId(TestResources.DOC_TYPE_ID)
                .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
                .payloadDigest("ThisIsASHA256Digest".getBytes())
        ;


        // Signs and builds the REMEvidenceType instance
        SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKeyEntry);

        // Grabs the REMEvidenceType instance in order to make some assertions.
        REMEvidenceType remEvidenceInstance = signedRemEvidence.getRemEvidenceType();
        assertNotNull(remEvidenceInstance);

        ExtensionsListType extensions = signedRemEvidence.getRemEvidenceType().getExtensions();

        assertNull(extensions);
    }

    @Test
    public void testOptionalDocumentTypeInstanceId() throws Exception {

        RemEvidenceBuilder builder = remEvidenceService.createDeliveryNonDeliveryToRecipientBuilder();
        builder.eventCode(EventCode.ACCEPTANCE)
                .eventTime(new Date())
                .eventReason(EventReason.OTHER)
                .evidenceIssuerPolicyID(TestResources.EVIDENCE_ISSUER_POLICY_ID)
                .evidenceIssuerDetails(TestResources.EVIDENCE_ISSUER_NAME)
                .senderIdentifier(TestResources.SENDER_IDENTIFIER)
                .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
                .documentTypeId(TestResources.DOC_TYPE_ID)
                .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
                .payloadDigest("ThisIsASHA256Digest".getBytes())
                .protocolSpecificEvidence(TransmissionRole.C_3, TransportProtocol.AS2, specificReceiptBytes)
        ;


        // Signs and builds the REMEvidenceType instance
        SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKeyEntry);

        assertNull(signedRemEvidence.getDocumentTypeInstanceIdentifier());

    }
}
