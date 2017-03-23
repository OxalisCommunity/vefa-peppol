/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.common.api.PerformResult;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import no.difi.vefa.peppol.common.util.ExceptionUtil;
import no.difi.vefa.peppol.evidence.jaxb.receipt.PeppolRemExtension;
import no.difi.vefa.peppol.evidence.jaxb.rem.AttributedElectronicAddressType;
import no.difi.vefa.peppol.evidence.jaxb.rem.EntityDetailsListType;
import no.difi.vefa.peppol.evidence.jaxb.rem.EntityDetailsType;
import no.difi.vefa.peppol.evidence.jaxb.rem.REMEvidenceType;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import java.util.Date;
import java.util.List;

/**
 * Holds a signed REMEvidence. Internally it is held in two representations; REMEvidenceType and
 * W3C Document.
 * <p/>
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
     */
    public REMEvidenceType getRemEvidenceType() {
        return e();
    }

    public Document getDocument() {
        return signedRemEvidenceXml;
    }

    public EvidenceTypeInstance getEvidenceType() {
        return EvidenceTypeInstance.findByLocalName(signedRemEvidenceXml.getDocumentElement().getLocalName());
    }

    public String getEvidenceIdentifier() {
        return e().getEvidenceIdentifier();
    }

    public EventCode getEventCode() {
        return EventCode.valueFor(e().getEventCode());
    }

    public EventReason getEventReason() {
        return EventReason.valueForCode(e().getEventReasons().getEventReason().get(0).getCode());
    }

    public Date getEventTime() {
        return e().getEventTime().toGregorianCalendar().getTime();
    }

    public String getEvidenceIssuerPolicyID() throws RemEvidenceException {
        if (e().getEvidenceIssuerPolicyID() == null)
            throw new RemEvidenceException("Evidence issuer policy ID is not set");

        return e().getEvidenceIssuerPolicyID().getPolicyID().get(0);
    }

    public String getEvidenceIssuerDetails() throws RemEvidenceException {
        return ExceptionUtil.perform(RemEvidenceException.class, "There are no Event Issuer Details",
                new PerformResult<String>() {
                    @Override
                    public String action() throws Exception {
                        return e().getEvidenceIssuerDetails().getNamesPostalAddresses()
                                .getNamePostalAddress().get(0).getEntityName().getName().get(0);
                    }
                });
    }

    public ParticipantIdentifier getSenderIdentifier() {
        EntityDetailsType senderDetails = e().getSenderDetails();
        List<Object> attributedElectronicAddressOrElectronicAddress = senderDetails
                .getAttributedElectronicAddressOrElectronicAddress();

        return RemHelper.readElectronicAddressType((AttributedElectronicAddressType)
                attributedElectronicAddressOrElectronicAddress.get(0));
    }


    /**
     * Internal convenience method
     */
    private REMEvidenceType e() {
        return jaxbElement.getValue();
    }

    public ParticipantIdentifier getRecipientIdentifier() {
        EntityDetailsListType entityDetailsListType = e().getRecipientsDetails();
        EntityDetailsType entityDetailsType = entityDetailsListType.getEntityDetails().get(0);
        List<Object> objectList = entityDetailsType.getAttributedElectronicAddressOrElectronicAddress();

        return RemHelper.readElectronicAddressType((AttributedElectronicAddressType) objectList.get(0));
    }

    public DocumentTypeIdentifier getDocumentTypeIdentifier() {
        return DocumentTypeIdentifier.of(e().getSenderMessageDetails().getMessageSubject(), Scheme.NONE);
    }

    public String getDocumentTypeInstanceIdentifier() {
        return e().getSenderMessageDetails().getUAMessageIdentifier();
    }

    public InstanceIdentifier getInstanceIdentifier() {
        return InstanceIdentifier.of(e().getSenderMessageDetails().getMessageIdentifierByREMMD());
    }

    public byte[] getPayloadDigestValue() {
        return e().getSenderMessageDetails().getDigestValue();
    }

    public PeppolRemExtension getTransmissionEvidence() {
        return (PeppolRemExtension) e().getExtensions().getExtension().get(0).getContent().get(0);
    }
}
