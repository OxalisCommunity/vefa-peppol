package no.difi.vefa.peppol.evidence.rem;

import eu.peppol.xsd.ticc.receipt._1.OriginalReceiptType;
import eu.peppol.xsd.ticc.receipt._1.PeppolRemExtension;
import eu.peppol.xsd.ticc.receipt._1.TransmissionRole;
import java.security.KeyStore;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.security.api.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigSigner;
import org.etsi.uri._01903.v1_3.AnyType;
import org.etsi.uri._02640.v2_.*;
import org.w3._2000._09.xmldsig_.DigestMethodType;
import org.w3c.dom.Document;

/**
 * Builds instances of SignedRemEvidence based upon the properties supplied.
 * <p>
 * See unit tests for details on how to use it.
 * <p>
 * Created by steinar on 08.11.2015.
 * Edited by sanderf to fix issues #4, #5, #11
 */
public class RemEvidenceBuilder {

    public static final String REM_VERSION = "2";

    private final EvidenceTypeInstance evidenceTypeInstance;
    private final String version = REM_VERSION;
    private EventCode eventCode;
    private EventReason eventReason;
    private final String evidenceIdentifier = UUID.randomUUID().toString();
    private String evidenceIssuerDetails;
    private String evidenceIssuerPolicyID;
    
    // This will used to set the uaMessageIdentifier element
    private String documentTypeInstanceId;
    
    // The timestamp of the delivery, defaults to current date and time.
    private Date eventTime = new Date();

    private ParticipantIdentifier senderIdentifier;
    private ParticipantIdentifier recipientIdentifier;
    private DocumentTypeIdentifier documentTypeId;
    private InstanceIdentifier instanceIdentifier;
    private byte[] payloadDigest;
    private TransmissionRole transmissionRole;
    private TransportProtocol transportProtocol;
    private byte[] protocolSpecificBytes;


    protected RemEvidenceBuilder(final EvidenceTypeInstance evidenceTypeInstance) {
        this.evidenceTypeInstance = evidenceTypeInstance;
    }

    /**
     * Transforms the REMEvidenceType, which is an XML Type into an XML instance represented in Java as
     * a JAXBElement.
     *
     * @param remEvidenceType
     * @param evidenceTypeInstance
     * @return
     */
    static JAXBElement<REMEvidenceType> createRemEvidenceTypeXmlInstance(REMEvidenceType remEvidenceType, EvidenceTypeInstance evidenceTypeInstance) throws RemEvidenceException {
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
                throw new RemEvidenceException("Invalid or unsupported evidenceType " + evidenceTypeInstance);
        }
        return remEvidenceTypeXmlInstance;
    }

    /**
     * Injects the the PEPPOL extensions, which includes the bytes of the original specific transport receipt.
     *  @param remEvidenceType
     * @param transmissionRole
     * @param transportProtocol
     */
    static void injectPeppolExtensions(REMEvidenceType remEvidenceType, TransmissionRole transmissionRole, TransportProtocol transportProtocol, byte[] specificReceiptBytes) {
        // Include the original transport receipt
        org.etsi.uri._01903.v1_3.ObjectFactory objectFactory = new org.etsi.uri._01903.v1_3.ObjectFactory();

        // Extensions/Extension/Any
        ExtensionType extensionType = new ExtensionType();
        AnyType anyType = new AnyType();
        JAXBElement<AnyType> extensionAny = objectFactory.createAny(anyType);

        // //PeppolRemExtension
        PeppolRemExtension peppolRemExtension = new PeppolRemExtension();
        peppolRemExtension.setTransmissionProtocol(transportProtocol.getIdentifier());
        peppolRemExtension.setTransmissionRole(transmissionRole);
        OriginalReceiptType originalReceiptType = new OriginalReceiptType();
        originalReceiptType.setValue(specificReceiptBytes);
        peppolRemExtension.setOriginalReceipt(originalReceiptType);

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
     * @param documentTypeInstanceId
     * @param instanceIdentifier
     * @param payloadDigest
     */
    static void injectTransmissionMetaData(REMEvidenceType remEvidenceType, 
                                           String documentTypeId,
                                           String documentTypeInstanceId,
                                           String instanceIdentifier, byte[] payloadDigest) throws RemEvidenceException {
        // Sender message details
        MessageDetailsType messageDetailsType = new MessageDetailsType();
        remEvidenceType.setSenderMessageDetails(messageDetailsType);

        // Document type id from SBDH
        if (documentTypeId != null) {
            messageDetailsType.setMessageSubject(documentTypeId);
        } else
            throw new RemEvidenceException("Must supply document type identifier");

        // Document type instance id from SBDH
        if (documentTypeInstanceId != null)
            messageDetailsType.setUAMessageIdentifier(documentTypeInstanceId);
        
        // Instance identifier from SBDH
        if (instanceIdentifier != null) {
            messageDetailsType.setMessageIdentifierByREMMD(instanceIdentifier);
        } else
            throw new RemEvidenceException("Must supply message instance identifier");

        // The digest value of the actual payload
        DigestMethodType digestMethodType = new DigestMethodType();
        digestMethodType.setAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
        messageDetailsType.setDigestMethod(digestMethodType);

        if (payloadDigest != null) {
            messageDetailsType.setDigestValue(payloadDigest);
        } else
            throw new RemEvidenceException("Must supply the digest of the original payload of the SBDH");
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

    public RemEvidenceBuilder evidenceIssuerPolicyID(String evidencePolicyID) {
        this.evidenceIssuerPolicyID = evidencePolicyID;
        return this;
    }

    public RemEvidenceBuilder evidenceIssuerDetails(String evidenceIssuerDetails) {
        this.evidenceIssuerDetails = evidenceIssuerDetails;
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

    public RemEvidenceBuilder documentTypeId(DocumentTypeIdentifier documentTypeId) {
        this.documentTypeId = documentTypeId;
        return this;
    }

    public RemEvidenceBuilder documentTypeInstanceIdentifier(String documentTypeInstanceId) {
        this.documentTypeInstanceId = documentTypeInstanceId;
        return this;
    }
    
    /**
     * The value of <code>//DocumentIdentification/InstanceIdentifier</code> from the SBDH.
     *
     * @param instanceIdentifier the unique identification of the SBDH in accordance with UN/CEFACT TS SBDH
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

    public RemEvidenceBuilder protocolSpecificEvidence(TransmissionRole transmissionRole, TransportProtocol transportProtocol, byte[] protocolSpecificBytes) {
        this.transmissionRole = transmissionRole;
        this.transportProtocol = transportProtocol;
        this.protocolSpecificBytes = protocolSpecificBytes;
        return this;
    }


    /**
     * Builds an instance of SignedRemEvidence based upon the previously supplied parameters.
     *
     * @param privateKeyEntry the private key and certificate to be used for the XMLDsig signature
     * @return a signed RemEvidence represented as an instance of SignedRemEvidence
     * @throws RemEvidenceException when the properties provided are not correct or missing
     */
    public SignedRemEvidence buildRemEvidenceInstance(KeyStore.PrivateKeyEntry privateKeyEntry) throws RemEvidenceException {

        REMEvidenceType r = new REMEvidenceType();

        r.setVersion(version);

        //
        if (eventCode == null) {
            throw new RemEvidenceException("REM EventCode is required");
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
            throw new RemEvidenceException("Unable to set eventTime " + e.getMessage(), e);
        }
        
        // EvidencePolicyID
        if (evidenceIssuerPolicyID == null) 
            throw new RemEvidenceException("Evidence Issuer Policy ID missing");
        EvidenceIssuerPolicyIDType policyIDs = new EvidenceIssuerPolicyIDType();
        policyIDs.getPolicyID().add(evidenceIssuerPolicyID);
        r.setEvidenceIssuerPolicyID(policyIDs);
        
        // EvidenceIssuerDetails
        if (evidenceIssuerDetails == null)
            throw new RemEvidenceException("Issuer details missing");
        EntityNameType entityName = new EntityNameType();
        entityName.getName().add(evidenceIssuerDetails);
        NamePostalAddressType evidenceIssuerNameAndAddressType = new NamePostalAddressType();
        evidenceIssuerNameAndAddressType.setEntityName(entityName);
        NamesPostalAddressListType namesAndAddressList = new NamesPostalAddressListType();
        namesAndAddressList.getNamePostalAddress().add(evidenceIssuerNameAndAddressType);
        EntityDetailsType evidenceIssuerDetailsType = new EntityDetailsType();
        evidenceIssuerDetailsType.setNamesPostalAddresses(namesAndAddressList);
        r.setEvidenceIssuerDetails(evidenceIssuerDetailsType);
        
        // SenderDetails
        if (senderIdentifier != null) {

            AttributedElectronicAddressType sendersAttributedElectronicAddressType = new AttributedElectronicAddressType();
            sendersAttributedElectronicAddressType.setValue(senderIdentifier.toString());
            sendersAttributedElectronicAddressType.setScheme(senderIdentifier.getScheme().getValue());

            r.setSenderDetails(new EntityDetailsType());
            r.getSenderDetails().getAttributedElectronicAddressOrElectronicAddress().add(sendersAttributedElectronicAddressType);
        } else
            throw new RemEvidenceException("Sender details missing");

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


        injectTransmissionMetaData(r, documentTypeId.getIdentifier(), documentTypeInstanceId,
                                    instanceIdentifier.getValue(), payloadDigest);

        // Injects the transport level receipt (if supplied), i.e. AS2 MDN or AS4 Soap Header
        if (protocolSpecificBytes != null)
            injectPeppolExtensions(r, transmissionRole, transportProtocol, protocolSpecificBytes);

        // Creates the actual REMEvidenceType instance in accordance with the type of evidence specified.
        JAXBElement<REMEvidenceType> remEvidenceTypeXmlInstance = createRemEvidenceTypeXmlInstance(r, evidenceTypeInstance);

        // Signs the REMEvidenceType instance
        Document signedRemDocument = injectSignature(privateKeyEntry, remEvidenceTypeXmlInstance);

        // Transforms the REMEvidenceType DOM Document instance it's JAXB representation.
        JAXBElement<REMEvidenceType> remEvidenceTypeJAXBElement = RemEvidenceTransformer.toJaxb(signedRemDocument);

        return new SignedRemEvidence(remEvidenceTypeJAXBElement, signedRemDocument);
    }

    /**
     * Marshals the JAXBElement&lt;REMEvidenceType&gt; into a W3C DOM object, which is digitally signed.
     *
     * @param privateKeyEntry            the private key and certificate of the signer
     * @param remEvidenceTypeXmlInstance the REMEvidenceType instance to be signed
     * @return W3C DOM Document with the signature
     */
    Document injectSignature(KeyStore.PrivateKeyEntry privateKeyEntry, JAXBElement<REMEvidenceType> remEvidenceTypeXmlInstance) throws RemEvidenceException {

        // Marshals the JAXBElement into DOM object for signing
        Marshaller marshaller;
        try {
            marshaller = JaxbContextHolder.INSTANCE.getMarshaller();
        } catch (JAXBException e) {
            throw new RemEvidenceException("Unable to create marshaller for transformation into a DOM object for creating the signature", e);
        }


        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RemEvidenceException("Unable to create a DOM document builder", e);
        }
        Document unsignedRemEvidenceDocument = documentBuilder.newDocument();
        try {
            marshaller.marshal(remEvidenceTypeXmlInstance, unsignedRemEvidenceDocument);
        } catch (JAXBException e) {
            throw new RemEvidenceException("Unable to marshal RemEvidenceType into a DOM document");
        }

        // Performs the actual signing of the document
        Document signedRemEvidenceDocument = documentBuilder.newDocument();
        DOMResult domResult = new DOMResult(signedRemEvidenceDocument);
        try {
            XmldsigSigner.SHA256().sign(unsignedRemEvidenceDocument, privateKeyEntry, domResult);
        } catch (PeppolSecurityException e) {
            throw new RemEvidenceException("Unable to sign RemEvidenceType " + e.getMessage(), e);
        }

        return signedRemEvidenceDocument;
    }
}
