package no.difi.vefa.peppol.evidence.rem;

import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Internal test ensuring that the RemEvidenceBuilder works as expected.
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

        specificReceiptBytes = TestResources.getSampleMdnSmime();

        privateKeyEntry = TestResources.getPrivateKey();

        // Allows us the obtain the JAXBContext, which is needed when creating instances of RemEvidenceBuilder
        // RemEvidenceBuilder instances can only be created by using the factory, but since this is a unit test,
        // we are allowed to go behind the scenes.
        remEvidenceService = new RemEvidenceService();

    }

    @Test
    public void createSampleInstance() throws Exception {


        RemEvidenceBuilder builder = new RemEvidenceBuilder(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT, remEvidenceService.getJaxbContext());

        builder.eventCode(EventCode.ACCEPTANCE)
                .senderIdentifier(TestResources.SENDER_IDENTIFIER)
                .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
                .documentTypeId(TestResources.DOC_TYPE_ID)
                .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
                .payloadDigest("ThisIsASHA256Digest".getBytes())
                .transmissionEvidence(specificReceiptBytes)
        ;

        // Signs and builds the REMEvidenceType instance
        JAXBElement<REMEvidenceType> remEvidenceInstance = builder.buildRemEvidenceInstance(privateKeyEntry);


        // Transforms the rem evidence instance into an XML representation suitable for some checks.
        Marshaller marshaller = remEvidenceService.getJaxbContext().createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        marshaller.marshal(remEvidenceInstance, baos);

        String xmlOutput = baos.toString();
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

        REMEvidenceType value = remEvidenceInstance.getValue();
        assertEquals(value.getEventCode(), EventCode.ACCEPTANCE.getValue().toString());

    }


}