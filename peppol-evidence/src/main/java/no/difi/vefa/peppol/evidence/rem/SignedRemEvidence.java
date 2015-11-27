package no.difi.vefa.peppol.evidence.rem;

import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;

/**
 * Holds a signed REMEvidenceType instance in various representations, i.e. JAXBElement&lt;REMEvidenceType&gt; and
 * XML representation.
 *
 * <em>Note!</em> the JAXBElement is not suitable for signature verification.
 *
 * For signature verification, use the XML representation, which may be access via
 * {@link #getSignedRemEvidenceDocument()}
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

    public JAXBElement<REMEvidenceType> getJaxbElement() {
        return jaxbElement;
    }

    public Document getSignedRemEvidenceDocument() {
        return signedRemEvidenceXml;
    }
}
