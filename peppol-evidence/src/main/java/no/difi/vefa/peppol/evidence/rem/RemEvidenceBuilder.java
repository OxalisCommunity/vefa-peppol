/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.evidence.jaxb.receipt.OriginalReceiptType;
import no.difi.vefa.peppol.evidence.jaxb.receipt.PeppolRemExtension;
import no.difi.vefa.peppol.evidence.jaxb.receipt.TransmissionRole;
import no.difi.vefa.peppol.evidence.jaxb.rem.*;
import no.difi.vefa.peppol.evidence.jaxb.xades.AnyType;
import no.difi.vefa.peppol.evidence.jaxb.xmldsig.DigestMethodType;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigSigner;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import java.security.KeyStore;
import java.util.Date;

/**
 * Builds instances of SignedRemEvidence based upon the properties supplied.
 * <p/>
 * See unit tests for details on how to use it.
 * <p/>
 * Created by steinar on 08.11.2015.
 * Edited by sanderf to fix issues #4, #5, #11
 */
public class RemEvidenceBuilder {

    private Evidence evidence;

    private String evidenceIssuerPolicyID;

    protected RemEvidenceBuilder(final EvidenceTypeInstance evidenceTypeInstance) {
        evidence = Evidence.newInstance()
                .type(evidenceTypeInstance)
                .evidenceIdentifier(InstanceIdentifier.generateUUID())
                .timestamp(new Date());
    }

    /**
     * Injects the the PEPPOL extensions, which includes the bytes of the original specific transport receipt.
     */
    static void injectPeppolExtensions(REMEvidenceType remEvidenceType, TransmissionRole transmissionRole, TransportProtocol transportProtocol, byte[] specificReceiptBytes) {
        // Include the original transport receipt
        no.difi.vefa.peppol.evidence.jaxb.xades.ObjectFactory objectFactory = new no.difi.vefa.peppol.evidence.jaxb.xades.ObjectFactory();

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
        peppolRemExtension.getOriginalReceipt().add(originalReceiptType);

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
        evidence = evidence.eventCode(eventCode);
        return this;
    }

    /**
     * Spec says that multiple event reasons may be added in theory, however the details for each concrete instance indicates
     * a cardinality of 0..1
     */
    public RemEvidenceBuilder eventReason(EventReason eventReason) {
        evidence = evidence.eventReason(eventReason);
        return this;
    }

    public RemEvidenceBuilder eventTime(Date date) {
        evidence = evidence.timestamp(date);
        return this;
    }

    public RemEvidenceBuilder evidenceIssuerPolicyID(String evidencePolicyID) {
        this.evidenceIssuerPolicyID = evidencePolicyID;
        return this;
    }

    public RemEvidenceBuilder evidenceIssuerDetails(String evidenceIssuerDetails) {
        evidence = evidence.issuer(evidenceIssuerDetails);
        return this;
    }

    public RemEvidenceBuilder senderIdentifier(ParticipantIdentifier senderIdentifier) {
        evidence = evidence.sender(senderIdentifier);
        return this;
    }

    public RemEvidenceBuilder recipientIdentifer(ParticipantIdentifier recipientIdentifier) {
        evidence = evidence.receiver(recipientIdentifier);
        return this;
    }

    public RemEvidenceBuilder documentTypeId(DocumentTypeIdentifier documentTypeId) {
        evidence = evidence.documentTypeIdentifier(documentTypeId);
        return this;
    }

    public RemEvidenceBuilder documentTypeInstanceIdentifier(String documentTypeInstanceId) {
        evidence = evidence.documentIdentifier(InstanceIdentifier.of(documentTypeInstanceId));
        return this;
    }

    /**
     * The value of <code>//DocumentIdentification/InstanceIdentifier</code> from the SBDH.
     *
     * @param instanceIdentifier the unique identification of the SBDH in accordance with UN/CEFACT TS SBDH
     * @return reference to the builder
     */
    public RemEvidenceBuilder instanceIdentifier(InstanceIdentifier instanceIdentifier) {
        evidence = evidence.messageIdentifier(instanceIdentifier);
        return this;
    }

    public RemEvidenceBuilder payloadDigest(byte[] payloadDigest) {
        evidence = evidence.digest(Digest.of(null, payloadDigest));
        return this;
    }

    public RemEvidenceBuilder protocolSpecificEvidence(TransmissionRole transmissionRole, TransportProtocol transportProtocol, byte[] protocolSpecificBytes) {
        evidence = evidence
                .transmissionRole(transmissionRole)
                .transportProtocol(transportProtocol)
                .originalReceipt(Receipt.of(protocolSpecificBytes));
        return this;
    }


    /**
     * Builds an instance of SignedRemEvidence based upon the previously supplied parameters.
     *
     * @param privateKeyEntry the private key and certificate to be used for the XMLDsig signature
     * @return a signed RemEvidence represented as an instance of SignedRemEvidence
     * @throws RemEvidenceException when the properties provided are not correct or missing
     */
    public SignedRemEvidence buildRemEvidenceInstance(KeyStore.PrivateKeyEntry privateKeyEntry)
            throws RemEvidenceException {

        REMEvidenceType evidence = new REMEvidenceType();

        evidence.setVersion("2");

        //
        if (this.evidence.getEventCode() == null)
            throw new RemEvidenceException("REM EventCode is required");

        evidence.setEventCode(this.evidence.getEventCode().getValue());

        // EventReason
        if (this.evidence.getEventReason() != null) {
            evidence.setEventReasons(RemHelper.createEventReasonsType(this.evidence.getEventReason()));
        }

        // EvidenceIdentifier a unique identifier
        evidence.setEvidenceIdentifier(this.evidence.getEvidenceIdentifier().getValue());

        // Event time
        evidence.setEventTime(RemHelper.toXmlGregorianCalendar(this.evidence.getTimestamp()));

        // EvidencePolicyID
        if (evidenceIssuerPolicyID == null)
            throw new RemEvidenceException("Evidence Issuer Policy ID missing");

        EvidenceIssuerPolicyIDType policyIDs = new EvidenceIssuerPolicyIDType();
        policyIDs.getPolicyID().add(evidenceIssuerPolicyID);
        evidence.setEvidenceIssuerPolicyID(policyIDs);

        // EvidenceIssuerDetails
        if (this.evidence.getIssuer() == null)
            throw new RemEvidenceException("Issuer details missing");

        EntityNameType entityName = new EntityNameType();
        entityName.getName().add(this.evidence.getIssuer());
        NamePostalAddressType evidenceIssuerNameAndAddressType = new NamePostalAddressType();
        evidenceIssuerNameAndAddressType.setEntityName(entityName);
        NamesPostalAddressListType namesAndAddressList = new NamesPostalAddressListType();
        namesAndAddressList.getNamePostalAddress().add(evidenceIssuerNameAndAddressType);
        EntityDetailsType evidenceIssuerDetailsType = new EntityDetailsType();
        evidenceIssuerDetailsType.setNamesPostalAddresses(namesAndAddressList);
        evidence.setEvidenceIssuerDetails(evidenceIssuerDetailsType);

        // SenderDetails
        if (this.evidence.getSender() == null)
            throw new RemEvidenceException("Sender details missing");

        evidence.setSenderDetails(new EntityDetailsType());
        evidence.getSenderDetails().getAttributedElectronicAddressOrElectronicAddress().add(
                RemHelper.createElectronicAddressType(this.evidence.getSender()));

        // Receiver details
        if (this.evidence.getReceiver() == null)
            throw new RemEvidenceException("Receiver details missing");

        evidence.setRecipientsDetails(new EntityDetailsListType());
        EntityDetailsType entityDetailsType = new EntityDetailsType();
        entityDetailsType.getAttributedElectronicAddressOrElectronicAddress().add(
                RemHelper.createElectronicAddressType(this.evidence.getReceiver()));
        evidence.getRecipientsDetails().getEntityDetails().add(entityDetailsType);


        injectTransmissionMetaData(evidence, this.evidence.getDocumentTypeIdentifier().getIdentifier(),
                this.evidence.getDocumentIdentifier() == null ? null : this.evidence.getDocumentIdentifier().getValue(),
                this.evidence.getMessageIdentifier().getValue(), this.evidence.getDigest().getValue());

        // Injects the transport level receipt (if supplied), i.e. AS2 MDN or AS4 Soap Header
        if (this.evidence.getOriginalReceipts().size() > 0)
            injectPeppolExtensions(evidence, this.evidence.getTransmissionRole(), this.evidence.getTransportProtocol(), this.evidence.getOriginalReceipts().get(0).getValue());

        // Creates the actual REMEvidenceType instance in accordance with the type of evidence specified.
        JAXBElement<REMEvidenceType> remEvidenceTypeXmlInstance = this.evidence.getType().toJAXBElement(evidence);

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
            marshaller = RemHelper.getMarshaller();
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
