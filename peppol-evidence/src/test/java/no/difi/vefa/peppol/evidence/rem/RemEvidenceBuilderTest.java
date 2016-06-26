package no.difi.vefa.peppol.evidence.rem;

import eu.peppol.xsd.ticc.receipt._1.PeppolRemExtension;
import eu.peppol.xsd.ticc.receipt._1.TransmissionRole;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.util.Date;
import javax.xml.bind.JAXBElement;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.etsi.uri._01903.v1_3.AnyType;
import org.etsi.uri._02640.v2_.ExtensionType;
import org.etsi.uri._02640.v2_.ExtensionsListType;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
        assertEquals(remEvidenceInstance.getVersion(), RemEvidenceBuilder.REM_VERSION);
        // ------------- Issue #2 --------------


        assertEquals(remEvidenceInstance.getEventCode(), EventCode.ACCEPTANCE.getValue().toString());

        // Transforms the rem evidence instance into an XML representation suitable for some checks.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new RemEvidenceTransformer().toFormattedXml(signedRemEvidence, baos);
        String xmlOutput = baos.toString("UTF-8");

        System.out.println(xmlOutput);

        assertTrue(xmlOutput.contains(TestResources.DOC_TYPE_ID.getIdentifier()), "Document type id has not been included in the REM XML");
        assertTrue(xmlOutput.contains(TestResources.INSTANCE_IDENTIFIER.getValue()), "Instance identifier missing");
        assertTrue(xmlOutput.contains(TestResources.SENDER_IDENTIFIER.getIdentifier()), "Sender identifier missing in generated xml");
        assertTrue(xmlOutput.contains(TestResources.RECIPIENT_IDENTIFIER.getIdentifier()), "Recipient identifier missing in generated xml");

        // Verifies that the signature was created, note that we omit the opening '<' as we have no idea
        // what the namespace might be.
        // If we were a little more eager we would use XPath to verify the contents :-)

        assertTrue(xmlOutput.contains("SignatureValue>"));
        assertTrue(xmlOutput.contains("KeyInfo>"));

        // Verifies the signature using the W3C Document
        XmldsigVerifier.verify(signedRemEvidence.getDocument());

        EventCode eventCode = signedRemEvidence.getEventCode();
        EventReason eventReason = signedRemEvidence.getEventReason();
        Date eventTime = signedRemEvidence.getEventTime();
        
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
        ;


        // Signs and builds the REMEvidenceType instance
        SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKeyEntry);

        // Grabs the REMEvidenceType instance in order to make some assertions.
        REMEvidenceType remEvidenceInstance = signedRemEvidence.getRemEvidenceType();


        ExtensionType extensionType = signedRemEvidence.getRemEvidenceType().getExtensions().getExtension().get(0);
        JAXBElement<AnyType> anyTypeJAXBElement = (JAXBElement<AnyType>) extensionType.getContent().get(0);
        AnyType value = anyTypeJAXBElement.getValue();

        assertTrue(value.getContent().get(0) instanceof PeppolRemExtension);

        PeppolRemExtension peppolRemExtension = (PeppolRemExtension) value.getContent().get(0);
        byte[] evidenceBytes = peppolRemExtension.getOriginalReceipt().getValue();

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
