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
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.TransportProtocol;
import no.difi.vefa.peppol.evidence.jaxb.receipt.TransmissionRole;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Properties;

import static org.testng.Assert.assertNotNull;

/**
 * Created by soc on 06.11.2015.
 */
public class TestResources {

    public static final DocumentTypeIdentifier DOC_TYPE_ID = DocumentTypeIdentifier.of(
            "urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##" +
                    "urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1");

    public static final String DOC_TYPE_INSTANCE_ID = "doc-type-instance-id";

    public static final InstanceIdentifier INSTANCE_IDENTIFIER = InstanceIdentifier.generateUUID();

    public static final ParticipantIdentifier SENDER_IDENTIFIER = ParticipantIdentifier.of("9908:810017902");

    public static final ParticipantIdentifier RECIPIENT_IDENTIFIER = ParticipantIdentifier.of("9908:123456789");

    public static final String EVIDENCE_ISSUER_POLICY_ID = "http://ev_policyid.issuer.test/clause15";

    public static final String EVIDENCE_ISSUER_NAME = "RemBuilderTest";

    private static KeyStore keyStore;

    private static RemEvidenceService remEvidenceService;

    /**
     * Convenient helper method to obtain named Mime message resource from the class path
     *
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
            throw new IllegalStateException(
                    String.format("Unable to load mime message from resource %s class path: %s",
                            resourceName, e.getMessage()), e);
        } catch (IOException e) {
            throw new IllegalStateException(
                    String.format("Unable to write contents of mime message to byte array %s",
                            e.getMessage()), e);
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
            keyStore.load(
                    TestResources.class.getResourceAsStream("/keystore-self-signed.jks"), "changeit".toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new IllegalStateException("Unable to load data into keystore from 'keystore-self-signed.jks'", e);
        }

        return keyStore;
    }

    public static KeyStore.PrivateKeyEntry getPrivateKey() {
        KeyStore localKeyStore = getKeystore();
        KeyStore.PrivateKeyEntry privateKeyEntry;
        try {
            privateKeyEntry = (KeyStore.PrivateKeyEntry) localKeyStore.getEntry(
                    "self-signed", new KeyStore.PasswordProtection("changeit".toCharArray()));
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            throw new IllegalStateException("Unable to load private key entry with alias 'self-signed'", e);
        }

        return privateKeyEntry;
    }


    /**
     * Creates sample rem evidence.
     *
     * @return sample REMEvidence based upon the resources in test/resources of this project.
     * @throws RemEvidenceException
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
