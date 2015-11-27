package no.difi.vefa.peppol.evidence.rem;

import eu.peppol.xsd.ticc.receipt._1.OriginalReceiptType;
import eu.peppol.xsd.ticc.receipt._1.PeppolRemExtensionType;
import eu.peppol.xsd.ticc.receipt._1.TransmissionRole;
import no.difi.vefa.peppol.common.model.DocumentIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.security.api.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigSigner;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.etsi.uri._01903.v1_3.*;
import org.etsi.uri._02640.v2_.*;
import org.etsi.uri._02640.v2_.ObjectFactory;
import org.w3._2000._09.xmldsig_.DigestMethodType;
import org.w3c.dom.Document;

import javax.xml.bind.*;
import javax.xml.bind.util.JAXBResult;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Builds instances of REMEvidenceType represented as instances of JAXBElement&lt;REMEvidenceType&gt;
 *
 * NOTE! Use RemEvidenceBuilder
 *
 * Created by steinar on 08.11.2015.
 *
 * See unit tests for details on how to use it.
 */
class RemEvidenceBuilder {

    private String version;
    private final EvidenceTypeInstance evidenceTypeInstance;
    private final JAXBContext jaxbContext;
    private EventCode eventCode;
    private EventReason eventReason;
    private String evidenceIdentifier = UUID.randomUUID().toString();

    // The timestamp of the delivery, defaults to current date and time.
    private Date eventTime = new Date();

    private ParticipantIdentifier senderIdentifier;
    private ParticipantIdentifier recipientIdentifier;
    private DocumentIdentifier documentTypeId;
    private InstanceIdentifier instanceIdentifier;
    private byte[] payloadDigest;
    private byte[] specificReceiptBytes;


    public RemEvidenceBuilder(final EvidenceTypeInstance evidenceTypeInstance, final JAXBContext jaxbContext) {
        this.evidenceTypeInstance = evidenceTypeInstance;
        this.jaxbContext = jaxbContext;
    }

    public RemEvidenceBuilder eventCode(EventCode eventCode) {
        this.eventCode = eventCode;
        return this;
    }


    /**
     * Spec says that multiple event reasons may be added in theory, however the details for each concrete instance indicates
     * a cardinality of 0..1
     *
     * @param eventReason
     * @return
     */
    public RemEvidenceBuilder eventReason(EventReason eventReason) {
        this.eventReason = eventReason;
        return this;
    }

    public RemEvidenceBuilder eventTime(Date date) {
        this.eventTime = date;
        return this;
    }

    public RemEvidenceBuilder senderIdentifier(ParticipantIdentifier senderIdentifier) {
        this.senderIdentifier = senderIdentifier;
        return this;
    }

    public RemEvidenceBuilder recipientIdentifer(ParticipantIdentifier recipientIdentifier) {
        this.recipientIdentifier = recipientIdentifier;
        return this;
    }

    public RemEvidenceBuilder documentTypeId(DocumentIdentifier documentTypeId) {

        this.documentTypeId = documentTypeId;
        return this;
    }

    /**
     * The value of <code>//DocumentIdentification/InstanceIdentifier</code> from the SBDH.
     *
     * @param instanceIdentifier the unique identification of the SBDH in accordance with UN/CEFACT TS SBDH
     *
     * @return reference to the builder
     */
    public RemEvidenceBuilder instanceIdentifier(InstanceIdentifier instanceIdentifier) {
        this.instanceIdentifier = instanceIdentifier;
        return this;
    }

    public RemEvidenceBuilder payloadDigest(byte[] payloadDigest) {
        this.payloadDigest = payloadDigest;
        return this;
    }

    public RemEvidenceBuilder transmissionEvidence(byte[] specificReceiptBytes) {
        this.specificReceiptBytes = specificReceiptBytes;
        return this;
    }


    public JAXBElement<REMEvidenceType> buildRemEvidenceInstance(KeyStore.PrivateKeyEntry privateKeyEntry) {

        REMEvidenceType r = new REMEvidenceType();


        r.setVersion(version);

        //
        if (eventCode == null) {
            throw new IllegalStateException("REM EventCode is required");
        }
        r.setEventCode(eventCode.getValue().toString());

        // EventReason
        if (eventReason != null) {
            EventReasonType eventReasonType = new EventReasonType();
            eventReasonType.setCode(eventReason.getCode());
            eventReasonType.setDetails(eventReason.getDetails());
            EventReasonsType reasonsType = new EventReasonsType();
            reasonsType.getEventReason().add(eventReasonType);
            r.setEventReasons(reasonsType);
        }

        // EvidenceIdentifier a unique identifier
        r.setEvidenceIdentifier(evidenceIdentifier);

        // Event time
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(eventTime);
        try {
            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            r.setEventTime(date2);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("Unable to set eventTime " + e.getMessage(), e);
        }

        // SenderDetails
        if (senderIdentifier != null) {

            AttributedElectronicAddressType sendersAttributedElectronicAddressType = new AttributedElectronicAddressType();
            sendersAttributedElectronicAddressType.setValue(senderIdentifier.toString());
            sendersAttributedElectronicAddressType.setScheme(senderIdentifier.getScheme().getValue());

            r.setSenderDetails(new EntityDetailsType());
            r.getSenderDetails().getAttributedElectronicAddressOrElectronicAddress().add(sendersAttributedElectronicAddressType);
        } else
            throw new IllegalStateException("Sender details missing");

        // Receiver details
        if (recipientIdentifier != null) {
            AttributedElectronicAddressType recipientsAttributedElectronicAddressType = new AttributedElectronicAddressType();
            recipientsAttributedElectronicAddressType.setValue(recipientIdentifier.toString());
            recipientsAttributedElectronicAddressType.setScheme(recipientIdentifier.getScheme().getValue());

            r.setRecipientsDetails(new EntityDetailsListType());
            EntityDetailsType entityDetailsType = new EntityDetailsType();
            entityDetailsType.getAttributedElectronicAddressOrElectronicAddress().add(recipientsAttributedElectronicAddressType);
            r.getRecipientsDetails().getEntityDetails().add(entityDetailsType);

        }


        injectTransmissionMetaData(r, documentTypeId.getIdentifier(), instanceIdentifier.getValue(), payloadDigest);

        // Injects the transport level receipt, i.e. AS2 MDN or AS4 Soap Headers
        injectPeppolExtensions(r, specificReceiptBytes);

        // Creates the actual REMEvidenceType instance in accordance with the type of evidence specified.
        JAXBElement<REMEvidenceType> remEvidenceTypeXmlInstance = createRemEvidenceTypeXmlInstance(r, evidenceTypeInstance);

        JAXBElement<REMEvidenceType> signedRemEvidenceTypeXmlInstance = injectSignature(privateKeyEntry, remEvidenceTypeXmlInstance);


        return signedRemEvidenceTypeXmlInstance;
    }


    /**
     * Marshals the JAXBElement&lt;REMEvidenceType&gt; into a W3C DOM object, which is digitally signed.
     *
     * @param privateKeyEntry the private key and certificate of the signer
     * @param remEvidenceTypeXmlInstance the REMEvidenceType instance to be signed
     *
     * @return W3C DOM Document with the signature
     */
    private JAXBElement<REMEvidenceType> injectSignature(KeyStore.PrivateKeyEntry privateKeyEntry, JAXBElement<REMEvidenceType> remEvidenceTypeXmlInstance)   {
        // Convert into DOM object for signing
        Marshaller marshaller = null;
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create marshaller for transformation into a DOM object for creating the signature",e);
        }


        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Unable to create a DOM document builder", e);
        }
        Document unsignedRemEvidenceDocument = documentBuilder.newDocument();
        try {
            marshaller.marshal(remEvidenceTypeXmlInstance, unsignedRemEvidenceDocument);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to marshal RemEvidenceType into a DOM document");
        }


        // Creates object to hold our signed result
        JAXBResult signedResult = null;
        try {
            signedResult = new JAXBResult(jaxbContext);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create a JAXBResult object into which the signed contents should be placed",e);
        }

        // Performs the actual signing of the document
        Document signedRemEvidenceDocument = documentBuilder.newDocument();
        DOMResult domResult = new DOMResult(signedRemEvidenceDocument);
        try {

            // XmldsigSigner.sign(unsignedRemEvidenceDocument, privateKeyEntry, signedResult);
            XmldsigSigner.sign(unsignedRemEvidenceDocument, privateKeyEntry, domResult);
        } catch (PeppolSecurityException e) {
            throw new IllegalStateException("Unable to sign RemEvidenceType " + e.getMessage(), e);
        }

        // TODO: Remove this once everything works.
        try {
            XmldsigVerifier.verify(signedRemEvidenceDocument);
        } catch (PeppolSecurityException e) {
            throw new IllegalStateException("Verification failed");
        }

        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            DOMSource source = new DOMSource(domResult.getNode());
            StreamResult result = new StreamResult(new FileOutputStream("/tmp/rem-dom.xml"));
            t.transform(source, result);
        } catch (TransformerException e) {
            throw new IllegalStateException("Unable to transform DOM Document to text");
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Unable to create file ");
        }

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<REMEvidenceType> remEvidenceTypeJAXBElement = unmarshaller.unmarshal(signedRemEvidenceDocument, REMEvidenceType.class);
            marshaller.marshal(remEvidenceTypeJAXBElement, new FileOutputStream("/tmp/rem-jaxb.xml"));
            return remEvidenceTypeJAXBElement;
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create unmarshaller");
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Unable to create REM output file");
        }

        /**
        try {
            return (JAXBElement<REMEvidenceType>)signedResult.getResult();
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to obtain result from JAXBResult and cast into JAXBElement<REMEvidenceType>", e);
        }
         */
    }

    /**
     * Transforms the REMEvidenceType, which is an XML Type into an XML instance represented in Java as
     * a JAXBElement.
     *
     * @param remEvidenceType
     * @param evidenceTypeInstance
     * @return
     */
    static JAXBElement<REMEvidenceType> createRemEvidenceTypeXmlInstance(REMEvidenceType remEvidenceType, EvidenceTypeInstance evidenceTypeInstance) {
        JAXBElement<REMEvidenceType> remEvidenceTypeXmlInstance;
        ObjectFactory objectFactory = new ObjectFactory();
        switch (evidenceTypeInstance) {
            case DELIVERY_NON_DELIVERY_TO_RECIPIENT:
                 remEvidenceTypeXmlInstance = objectFactory.createDeliveryNonDeliveryToRecipient(remEvidenceType);
                break;
            case RELAY_REM_MD_ACCEPTANCE_REJECTION:
                remEvidenceTypeXmlInstance = objectFactory.createRelayREMMDAcceptanceRejection(remEvidenceType);
                break;
            default:
                throw new IllegalStateException("Invalid or unsupported evidenceType " + evidenceTypeInstance);
        }
        return remEvidenceTypeXmlInstance;
    }


    /**
     * Injects the the PEPPOL extensions, which includes the bytes of the original specific transport receipt.
     *
     * @param remEvidenceType
     */
    static void injectPeppolExtensions(REMEvidenceType remEvidenceType, byte[] specificReceiptBytes) {
        // Include the original transport receipt
        org.etsi.uri._01903.v1_3.ObjectFactory objectFactory = new org.etsi.uri._01903.v1_3.ObjectFactory();

        // Extensions/Extension/Any
        ExtensionType extensionType = new ExtensionType();
        AnyType anyType = new AnyType();
        JAXBElement<AnyType> extensionAny = objectFactory.createAny(anyType);

        // //PeppolRemExtension
        PeppolRemExtensionType peppolRemExtensionType = new PeppolRemExtensionType();
        peppolRemExtensionType.setTransmissionProtocol("AS2");
        peppolRemExtensionType.setTransmissionRole(TransmissionRole.C_3);
        OriginalReceiptType originalReceiptType = new OriginalReceiptType();
        originalReceiptType.setValue(specificReceiptBytes);
        peppolRemExtensionType.setOriginalReceipt(originalReceiptType);

        eu.peppol.xsd.ticc.receipt._1.ObjectFactory peppolRemExtensionObjectFactory = new eu.peppol.xsd.ticc.receipt._1.ObjectFactory();
        JAXBElement<PeppolRemExtensionType> peppolRemExtension = peppolRemExtensionObjectFactory.createPeppolRemExtension(peppolRemExtensionType);

        // xpath: //Any/PeppolRemExtension
        anyType.getContent().add(peppolRemExtension);

        // xpath: //Extension/Any/PeppolRemExtension
        extensionType.getContent().add(extensionAny);

        // xpath: //Extensions/Extension/Any/PeppolRemExtension
        remEvidenceType.setExtensions(new ExtensionsListType());
        remEvidenceType.getExtensions().getExtension().add(extensionType);
    }

    /**
     * Injects the details of the transmission meta data, which are typically contained within the SBDH.
     *
     * @param remEvidenceType
     * @param documentTypeId
     * @param instanceIdentifier
     * @param payloadDigest
     */
    static void injectTransmissionMetaData(REMEvidenceType remEvidenceType, String documentTypeId, String instanceIdentifier, byte[] payloadDigest) {
        // Sender message details
        MessageDetailsType messageDetailsType = new MessageDetailsType();
        remEvidenceType.setSenderMessageDetails(messageDetailsType);

        // Document type id from SBDH
        if (documentTypeId != null) {
            messageDetailsType.setMessageSubject(documentTypeId);
        } else
            throw new IllegalStateException("Must supply document type identifier");

        // Instance identifier from SBDH
        if (instanceIdentifier != null) {
            messageDetailsType.setUAMessageIdentifier(instanceIdentifier);
        } else
            throw new IllegalStateException("Must supply message instance identifier");

        // The digest value of the actual payload
        DigestMethodType digestMethodType = new DigestMethodType();
        digestMethodType.setAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
        messageDetailsType.setDigestMethod(digestMethodType);

        if (payloadDigest != null) {
            messageDetailsType.setDigestValue(payloadDigest);
        } else
            throw new IllegalStateException("Must supply the digest of the original payload of the SBDH");
    }
}
