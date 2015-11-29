package no.difi.vefa.peppol.evidence.rem;

import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;

/**
 * Holds a signed REMEvidence. Internally it is held in two representations; REMEvidenceType and
 * W3C Document.
 *
 * Only the W3C Document representation may be used for signature validation
 * {@link #getDocument()}
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

    public REMEvidenceType getRemEvidenceType() {
        return jaxbElement.getValue();
    }

    public Document getDocument() {
        return signedRemEvidenceXml;
    }
}
