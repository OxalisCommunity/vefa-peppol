package no.difi.vefa.peppol.evidence.rem;

import eu.peppol.xsd.ticc.receipt._1.TransmissionRole;
import no.difi.vefa.peppol.common.model.TransportProfile;
import no.difi.vefa.peppol.common.model.TransportProtocol;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.testng.annotations.Test;

import java.security.KeyStore;

import static org.junit.Assert.assertNotNull;

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
                .protocolSpecificEvidence(TransmissionRole.C_3, TransportProtocol.AS2, "Jabla jabla fake MDN".getBytes());

        ;

        // Signs and builds the REMEvidenceType instance
        SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKey);

        assertNotNull(signedRemEvidence);

        XmldsigVerifier.verify(signedRemEvidence.getDocument());
    }
}