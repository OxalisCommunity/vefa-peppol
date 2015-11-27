package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.common.util.DomUtils;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;

import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

/**
 * Created by steinar on 08.11.2015.
 */
public class RemEvidenceServiceTest {


    @Test
    public void testCreaterelayRemMdAcceptanceRejectionBuilder() throws Exception {

        RemEvidenceService remEvidenceService = new RemEvidenceService();

        RemEvidenceBuilder builder = remEvidenceService.createDeliveryNonDeliveryToRecipientBuilder();

        byte[] sampleMdnSmime = TestResources.getSampleMdnSmime();
        KeyStore.PrivateKeyEntry privateKey = TestResources.getPrivateKey();

        builder.eventCode(EventCode.ACCEPTANCE)
                .senderIdentifier(TestResources.SENDER_IDENTIFIER)
                .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
                .documentTypeId(TestResources.DOC_TYPE_ID)
                .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
                .payloadDigest("ThisIsASHA256Digest".getBytes())
                .transmissionEvidence(sampleMdnSmime)
        ;

        // Signs and builds the REMEvidenceType instance
        SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKey);

        assertNotNull(signedRemEvidence);

        assertEquals(signedRemEvidence.getJaxbElement().getDeclaredType(),  REMEvidenceType.class);

        XmldsigVerifier.verify(signedRemEvidence.getSignedRemEvidenceDocument());
    }
}