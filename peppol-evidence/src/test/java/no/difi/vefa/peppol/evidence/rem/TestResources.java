package no.difi.vefa.peppol.evidence.rem;

import eu.peppol.xsd.ticc.receipt._1.TransmissionRole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import no.difi.vefa.peppol.common.model.*;
import static org.testng.Assert.assertNotNull;

/**
 * Created by soc on 06.11.2015.
 */
public class TestResources {

    public static final DocumentTypeIdentifier DOC_TYPE_ID = new DocumentTypeIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1");
    public static final String DOC_TYPE_INSTANCE_ID = "doc-type-instance-id";
    public static final InstanceIdentifier INSTANCE_IDENTIFIER = InstanceIdentifier.generateUUID();
    public static final ParticipantIdentifier SENDER_IDENTIFIER = new ParticipantIdentifier("9908:810017902");
    public static final ParticipantIdentifier RECIPIENT_IDENTIFIER = new ParticipantIdentifier("9908:123456789");

    public static final String EVIDENCE_ISSUER_POLICY_ID = "http://ev_policyid.issuer.test/clause15";
    public static final String EVIDENCE_ISSUER_NAME = "RemBuilderTest";
    
    private static KeyStore keyStore = null;
    private static RemEvidenceService remEvidenceService;


    /**
     * Convenient helper method to obtain named Mime message resource from the class path
     * @param resourceName
     * @return 
     * @throws javax.mail.MessagingException
     */
    public static MimeMessage getMimeMessageFromResource(String resourceName) throws MessagingException {
        InputStream resourceAsStream = TestResourcesTest.class.getClassLoader().getResourceAsStream(resourceName);
        assertNotNull(resourceAsStream);

        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties, null);
        return new MimeMessage(session, resourceAsStream);
    }


    public static byte[] getSampleMdnSmime() {
        return getMimeMessageFromResourceAsBytes("as2-mdn-smime.txt");
    }

    public static byte[] getMimeMessageFromResourceAsBytes(String resourceName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            MimeMessage mimeMessage = getMimeMessageFromResource(resourceName);
            mimeMessage.writeTo(baos);

        } catch (MessagingException e) {
            throw new IllegalStateException("Unable to load mime message from resource " + resourceName + " class path: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write contents of mime message to byte array " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    public static synchronized RemEvidenceService getRemEvidenceService() {
        if (remEvidenceService == null) {
            remEvidenceService = new RemEvidenceService();
        }

        return remEvidenceService;
    }

    public static synchronized KeyStore getKeystore() {
        if (keyStore != null) {
            return keyStore;
        }

        try {
            keyStore = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Unable to create KeyStore instance ", e);
        }
        try {
            keyStore.load(TestResources.class.getResourceAsStream("/keystore-self-signed.jks"), "changeit".toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new IllegalStateException("Unable to load data into keystore from 'keystore-self-signed.jks'", e);
        }

        return keyStore;
    }

    public static KeyStore.PrivateKeyEntry getPrivateKey() {
        KeyStore localKeyStore = getKeystore();
        KeyStore.PrivateKeyEntry privateKeyEntry;
        try {
            privateKeyEntry = (KeyStore.PrivateKeyEntry) localKeyStore.getEntry("self-signed", new KeyStore.PasswordProtection("changeit".toCharArray()));
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            throw new IllegalStateException("Unable to load private key entry with alias 'self-signed'", e);
        }

        return privateKeyEntry;
    }


    /**
     * Creates sample rem evidence.
     *
     * @return sample REMEvidence based upon the resources in test/resources of this project.
     * @throws no.difi.vefa.peppol.evidence.rem.RemEvidenceException
     */
    public static SignedRemEvidence createSampleRemEvidence() throws RemEvidenceException {
        RemEvidenceBuilder builder = remEvidenceService.createDeliveryNonDeliveryToRecipientBuilder();

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
                .protocolSpecificEvidence(TransmissionRole.C_3, TransportProtocol.AS2, sampleMdnSmime);

        
            return builder.buildRemEvidenceInstance(privateKey);
        
    }
}
