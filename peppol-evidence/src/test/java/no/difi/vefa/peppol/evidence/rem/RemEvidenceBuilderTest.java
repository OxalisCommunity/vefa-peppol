package no.difi.vefa.peppol.evidence.rem;

import eu.peppol.xsd.ticc.receipt._1.PeppolRemExtension;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.etsi.uri._01903.v1_3.AnyType;
import org.etsi.uri._02640.v2_.ExtensionType;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.Assert.assertNotNull;

/**
 * Ensures that the RemEvidenceBuilder works as expected.
 *
 * @author steinar
 *         Date: 04.11.2015
 *         Time: 21.05
 */
public class RemEvidenceBuilderTest    {

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

        RemEvidenceBuilder builder = new RemEvidenceBuilder(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT, remEvidenceService.getJaxbContext());

        builder.eventCode(EventCode.ACCEPTANCE)
                .eventTime(new Date())
                .eventReason(EventReason.OTHER)
                .senderIdentifier(TestResources.SENDER_IDENTIFIER)
                .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
                .documentTypeId(TestResources.DOC_TYPE_ID)
                .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
                .payloadDigest("ThisIsASHA256Digest".getBytes())
                .transmissionEvidence(specificReceiptBytes)
        ;


        // Signs and builds the REMEvidenceType instance
        SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKeyEntry);

        // Grabs the REMEvidenceType instance in order to make some assertions.
        REMEvidenceType remEvidenceInstance = signedRemEvidence.getRemEvidenceType();
        assertEquals(remEvidenceInstance.getEventCode(), EventCode.ACCEPTANCE.getValue().toString());

        // Transforms the rem evidence instance into an XML representation suitable for some checks.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        remEvidenceService.createRemEvidenceTransformer().toFormattedXml(signedRemEvidence,baos);
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

        // Verifies the signature using the rem evidence bytes
        XmldsigVerifier.verify(signedRemEvidence.getDocument());

        EventCode eventCode = signedRemEvidence.getEventCode();
        EventReason eventReason = signedRemEvidence.getEventReason();
        Date eventTime = signedRemEvidence.getEventTime();
        ParticipantIdentifier senderIdentifier = signedRemEvidence.getSenderIdentifier();
        assertNotNull(senderIdentifier);

        ParticipantIdentifier receiverIdentifier = signedRemEvidence.getRecipientIdentifier();
        assertNotNull(receiverIdentifier);


        DocumentTypeIdentifier documentTypeIdentifier = signedRemEvidence.getDocumentTypeIdentifier();
        assertNotNull(documentTypeIdentifier);

        InstanceIdentifier instanceIdentifier = signedRemEvidence.getInstanceIdentifier();
        assertNotNull(instanceIdentifier);

        byte[] digestBytes = signedRemEvidence.getPayloadDigestValue();
        assertNotNull(digestBytes);
        assertTrue(digestBytes.length > 0);

// TODO: fix the unmarshalling problem and uncomment this code.
//        PeppolRemExtensionType transmissionEvidence = signedRemEvidence.getTransmissionEvidence();
//        assertNotNull(transmissionEvidence);


    }


    @Test
    public void testUnmarshal() throws Exception {

            RemEvidenceBuilder builder = new RemEvidenceBuilder(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT, remEvidenceService.getJaxbContext());

            builder.eventCode(EventCode.ACCEPTANCE)
                    .eventTime(new Date())
                    .eventReason(EventReason.OTHER)
                    .senderIdentifier(TestResources.SENDER_IDENTIFIER)
                    .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
                    .documentTypeId(TestResources.DOC_TYPE_ID)
                    .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
                    .payloadDigest("ThisIsASHA256Digest".getBytes())
                    .transmissionEvidence(specificReceiptBytes)
            ;


            // Signs and builds the REMEvidenceType instance
            SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKeyEntry);

            // Grabs the REMEvidenceType instance in order to make some assertions.
            REMEvidenceType remEvidenceInstance = signedRemEvidence.getRemEvidenceType();


        // JAXB is unable to unmarshal the value of the AnyType into the correct Java type, thus forcing
        // me to perform yet another unmarshal operation
        ExtensionType extensionType = signedRemEvidence.getRemEvidenceType().getExtensions().getExtension().get(0);
        JAXBElement<AnyType> anyTypeJAXBElement = (JAXBElement<AnyType>) extensionType.getContent().get(0);
        AnyType value = anyTypeJAXBElement.getValue();


        // Unmarshaller unmarshaller = remEvidenceService.getJaxbContext().createUnmarshaller();
        // Node node = (Node) value.getContent().get(0);

        // Performs the correct unmarshalling
        // JAXBElement<PeppolRemExtension> peppolRemExtensionTypeJAXBElement = unmarshaller.unmarshal(node, PeppolRemExtension.class);
        assertTrue(value.getContent().get(0) instanceof PeppolRemExtension);

    }
}