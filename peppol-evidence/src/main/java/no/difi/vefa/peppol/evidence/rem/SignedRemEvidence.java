package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.EntityDetailsType;
import org.etsi.uri._02640.v2_.EventReasonType;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import java.util.Date;
import java.util.List;

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
    protected REMEvidenceType getRemEvidenceType() {
        return e();
    }

    protected Document getDocument() {
        return signedRemEvidenceXml;
    }

    public EventCode getEventCode() {
        return EventCode.valueFor(e().getEventCode());
    }

    public EventReason getEventReason() {
        assert e() != null : "jaxbElement.getValue() returned null";
        assert e().getEventReasons() != null : "There are no event reasons";
        assert e().getEventReasons().getEventReason() != null : "getEventReasons() returned null";
        assert e().getEventReasons().getEventReason().isEmpty() : "List of event reasons is empty";

        EventReasonType eventReasonType = e().getEventReasons().getEventReason().get(0);
        return EventReason.valueForCode(eventReasonType.getCode());
    }

    public Date getEventTime() {
        return e().getEventTime().toGregorianCalendar().getTime();
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

}
