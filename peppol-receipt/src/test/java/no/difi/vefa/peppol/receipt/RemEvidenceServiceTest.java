package no.difi.vefa.peppol.receipt;

import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBElement;
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
        JAXBElement<REMEvidenceType> remEvidenceInstance = builder.buildRemEvidenceInstance(privateKey);

        assertNotNull(remEvidenceInstance);

        assertEquals(remEvidenceInstance.getDeclaredType(),  REMEvidenceType.class);

    }
}