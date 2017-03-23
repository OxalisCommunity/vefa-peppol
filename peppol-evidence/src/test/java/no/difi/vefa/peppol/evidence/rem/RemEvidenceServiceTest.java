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

import no.difi.vefa.peppol.common.model.TransportProtocol;
import no.difi.vefa.peppol.evidence.jaxb.receipt.TransmissionRole;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.KeyStore;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by steinar on 08.11.2015.
 */
public class RemEvidenceServiceTest {

    protected byte[] specificReceiptBytes;

    protected KeyStore.PrivateKeyEntry privateKeyEntry;

    @BeforeClass
    public void setUp() {
        // Provides sample AS2 MDN to be included as evidence in the REM
        specificReceiptBytes = TestResources.getSampleMdnSmime();

        // Grabs our private key and certificate to be used for signing the REM
        privateKeyEntry = TestResources.getPrivateKey();
    }


    @Test
    public void testCreateDeliveryNonDeliveryToRecipientBuilder() throws Exception {

        RemEvidenceService remEvidenceService = new RemEvidenceService();

        RemEvidenceBuilder builder = remEvidenceService.createDeliveryNonDeliveryToRecipientBuilder();

        byte[] sampleMdnSmime = TestResources.getSampleMdnSmime();

        builder.eventCode(EventCode.ACCEPTANCE)
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

        assertNotNull(signedRemEvidence);
        assertEquals(signedRemEvidence.getEvidenceType(), EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT);

        XmldsigVerifier.verify(signedRemEvidence.getDocument());
    }

    @Test
    public void testCreateRelayRemMdAcceptanceRejectionBuilder() throws Exception {

        RemEvidenceService remEvidenceService = new RemEvidenceService();

        RemEvidenceBuilder builder = remEvidenceService.createRelayRemMdAcceptanceRejectionBuilder();

        byte[] sampleMdnSmime = TestResources.getSampleMdnSmime();
        KeyStore.PrivateKeyEntry privateKey = TestResources.getPrivateKey();

        builder.eventCode(EventCode.ACCEPTANCE)
                .evidenceIssuerPolicyID(TestResources.EVIDENCE_ISSUER_POLICY_ID)
                .evidenceIssuerDetails(TestResources.EVIDENCE_ISSUER_NAME)
                .senderIdentifier(TestResources.SENDER_IDENTIFIER)
                .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
                .documentTypeId(TestResources.DOC_TYPE_ID)
                .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
                .payloadDigest("ThisIsASHA256Digest".getBytes())
                .protocolSpecificEvidence(TransmissionRole.C_3, TransportProtocol.AS2,
                        "Jabla jabla fake MDN".getBytes());

        // Signs and builds the REMEvidenceType instance
        SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKey);

        assertNotNull(signedRemEvidence);
        assertEquals(signedRemEvidence.getEvidenceType(), EvidenceTypeInstance.RELAY_REM_MD_ACCEPTANCE_REJECTION);

        XmldsigVerifier.verify(signedRemEvidence.getDocument());
    }
}

