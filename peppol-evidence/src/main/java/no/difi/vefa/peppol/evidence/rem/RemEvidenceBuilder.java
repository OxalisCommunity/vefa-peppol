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

import no.difi.vefa.peppol.common.code.DigestMethod;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.evidence.jaxb.receipt.TransmissionRole;
import no.difi.vefa.peppol.evidence.jaxb.rem.REMEvidenceType;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
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

    protected RemEvidenceBuilder(final EvidenceTypeInstance evidenceTypeInstance) {
        evidence = Evidence.newInstance()
                .type(evidenceTypeInstance)
                .evidenceIdentifier(InstanceIdentifier.generateUUID())
                .timestamp(new Date());
    }

    public RemEvidenceBuilder eventCode(EventCode eventCode) {
        evidence = evidence.eventCode(eventCode);
        return this;
    }

    /**
     * Spec says that multiple event reasons may be added in theory, however the details for each
     * concrete instance indicates a cardinality of 0..1
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
        evidence = evidence.issuerPolicy(evidencePolicyID);
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
        evidence = evidence.digest(Digest.of(DigestMethod.SHA256, payloadDigest));
        return this;
    }

    public RemEvidenceBuilder protocolSpecificEvidence(TransmissionRole transmissionRole,
                                                       TransportProtocol transportProtocol,
                                                       byte[] protocolSpecificBytes) {
        evidence = evidence
                .transmissionRole(transmissionRole)
                .transportProtocol(transportProtocol)
                .originalReceipt(Receipt.of(protocolSpecificBytes));
        return this;
    }

    public Evidence getEvidence() {
        return evidence;
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
        try {
            // Signs the REMEvidenceType instance
            Document signedRemDocument = SignedEvidenceWriter.write(privateKeyEntry, this.evidence);

            // Transforms the REMEvidenceType DOM Document instance it's JAXB representation.
            JAXBElement<REMEvidenceType> remEvidenceTypeJAXBElement = RemEvidenceTransformer.toJaxb(signedRemDocument);

            return new SignedRemEvidence(remEvidenceTypeJAXBElement, signedRemDocument);
        } catch (PeppolSecurityException e) {
            throw new RemEvidenceException(e.getMessage(), e);
        }
    }
}
