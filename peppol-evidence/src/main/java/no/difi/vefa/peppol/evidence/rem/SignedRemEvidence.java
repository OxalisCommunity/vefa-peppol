package no.difi.vefa.peppol.evidence.rem;

import eu.peppol.xsd.ticc.receipt._1.PeppolRemExtension;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBElement;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import org.etsi.uri._01903.v1_3.AnyType;
import org.etsi.uri._02640.v2_.*;
import org.w3c.dom.Document;

/**
 * Holds a signed REMEvidence. Internally it is held in two representations; REMEvidenceType and
 * W3C Document.
 *
 * Please use {@link RemEvidenceTransformer} to transform instances of SignedRemEvidence into other
 * representations like for instance XML and JAXB
 *
 * @author steinar
 *         Date: 27.11.2015
 *         Time: 11.50
 */
public class SignedRemEvidence {

    private final JAXBElement<REMEvidenceType> jaxbElement;
    private final Document signedRemEvidenceXml;   
    
    public SignedRemEvidence(JAXBElement<REMEvidenceType> jaxbElement, Document signedRemEvidenceXml) {
        this.jaxbElement = jaxbElement;
        this.signedRemEvidenceXml = signedRemEvidenceXml;
    }

    /**
     * Provides access to the REM evidence in accordance with the XML schema. Thus allowing simple access to various
     * fields without reverting to XPath expressions in the W3C Document.
     *
     * @return
     */
    public REMEvidenceType getRemEvidenceType() {
        return e();
    }

    public Document getDocument() {
        return signedRemEvidenceXml;
    }

    public EvidenceTypeInstance getEvidenceType() {
        try {
            String evElementName = signedRemEvidenceXml.getDocumentElement().getLocalName();
            switch (evElementName) {
                case "DeliveryNonDeliveryToRecipient" : 
                    return EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT;
                case "RelayREMMDAcceptanceRejection" :
                    return EvidenceTypeInstance.RELAY_REM_MD_ACCEPTANCE_REJECTION;
                default:
                    return null;
             }
        } catch (NullPointerException npe) {
            return null;
        }
    }
    
    public String getEvidenceIdentifier() {
        return e().getEvidenceIdentifier();
    }
    
    public EventCode getEventCode() {
        return EventCode.valueFor(e().getEventCode());
    }

    public EventReason getEventReason() {
        assert e() != null : "jaxbElement.getValue() returned null";
        assert e().getEventReasons() != null : "There are no event reasons";
        assert e().getEventReasons().getEventReason() != null : "getEventReasons() returned null";
        assert !e().getEventReasons().getEventReason().isEmpty() : "List of event reasons is empty";

        EventReasonType eventReasonType = e().getEventReasons().getEventReason().get(0);
        return EventReason.valueForCode(eventReasonType.getCode());
    }

    public Date getEventTime() {
        return e().getEventTime().toGregorianCalendar().getTime();
    }
   
    public String getEvidenceIssuerPolicyID() throws RemEvidenceException {
        if (e().getEvidenceIssuerPolicyID() == null) 
            throw new RemEvidenceException("Evidence issuer policy ID is not set");
        else
            return e().getEvidenceIssuerPolicyID().getPolicyID().get(0);
    }
    
    public String getEvidenceIssuerDetails() throws RemEvidenceException {
        try {
            return e().getEvidenceIssuerDetails()
                            .getNamesPostalAddresses().getNamePostalAddress().get(0).getEntityName().getName().get(0);
        } catch (NullPointerException npe) {
            throw new RemEvidenceException("There are no Event Issuer Details");
        }            
    }
    
    public ParticipantIdentifier getSenderIdentifier() {

        EntityDetailsType senderDetails = e().getSenderDetails();
        List<Object> attributedElectronicAddressOrElectronicAddress = senderDetails.getAttributedElectronicAddressOrElectronicAddress();

        AttributedElectronicAddressType attributedElectronicAddressType = (AttributedElectronicAddressType) attributedElectronicAddressOrElectronicAddress.get(0);
        String scheme = attributedElectronicAddressType.getScheme();
        String value = attributedElectronicAddressType.getValue();

        return new ParticipantIdentifier(value, new Scheme(scheme));
    }


    /** Internal convenience method */
    private REMEvidenceType e() {
        return jaxbElement.getValue();
    }

    public ParticipantIdentifier getRecipientIdentifier() {
        EntityDetailsListType entityDetailsListType = e().getRecipientsDetails();
        EntityDetailsType entityDetailsType = entityDetailsListType.getEntityDetails().get(0);
        List<Object> objectList = entityDetailsType.getAttributedElectronicAddressOrElectronicAddress();

        AttributedElectronicAddressType attributedElectronicAddressType = (AttributedElectronicAddressType) objectList.get(0);
        String scheme = attributedElectronicAddressType.getScheme();
        String value = attributedElectronicAddressType.getValue();


        return new ParticipantIdentifier(value, new Scheme(scheme));
    }

    public DocumentTypeIdentifier getDocumentTypeIdentifier() {
        MessageDetailsType senderMessageDetails = e().getSenderMessageDetails();
        String messageSubject = senderMessageDetails.getMessageSubject();

        DocumentTypeIdentifier documentTypeIdentifier = new DocumentTypeIdentifier(messageSubject);

        return documentTypeIdentifier;
    }
    
    public String getDocumentTypeInstanceIdentifier() {
        return e().getSenderMessageDetails().getUAMessageIdentifier();
    }

    public InstanceIdentifier getInstanceIdentifier() {
        String remMDMessageIdentifier = e().getSenderMessageDetails().getMessageIdentifierByREMMD();

        return new InstanceIdentifier(remMDMessageIdentifier);
    }

    public byte[] getPayloadDigestValue() {
        assert e() != null : "jaxbElement.getValue() returned null";
        assert e().getSenderMessageDetails() != null : "getSenderMessageDetails() returned null";

        return e().getSenderMessageDetails().getDigestValue();
    }

    @SuppressWarnings("unchecked")
    public PeppolRemExtension getTransmissionEvidence() {

        ExtensionType extensionType = e().getExtensions().getExtension().get(0);

        JAXBElement<AnyType> anyType = (JAXBElement<AnyType>) extensionType.getContent().get(0);
        AnyType value = anyType.getValue();

        return (PeppolRemExtension) value.getContent().get(0);
    }
}
