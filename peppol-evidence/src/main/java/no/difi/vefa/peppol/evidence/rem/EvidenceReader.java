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
import no.difi.vefa.peppol.common.lang.PeppolException;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.evidence.jaxb.receipt.OriginalReceiptType;
import no.difi.vefa.peppol.evidence.jaxb.receipt.PeppolRemExtension;
import no.difi.vefa.peppol.evidence.jaxb.rem.AttributedElectronicAddressType;
import no.difi.vefa.peppol.evidence.jaxb.rem.ExtensionType;
import no.difi.vefa.peppol.evidence.jaxb.rem.REMEvidenceType;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

public class EvidenceReader {

    public static Evidence read(Node node) throws RemEvidenceException {
        return read(new DOMSource(node));
    }

    public static Evidence read(InputStream inputStream) throws RemEvidenceException {
        return read(new StreamSource(inputStream));
    }

    private static Evidence read(Source source) throws RemEvidenceException {
        try {
            Unmarshaller unmarshaller = RemHelper.getUnmarshaller();
            JAXBElement<REMEvidenceType> jaxbRemEvidence = unmarshaller.unmarshal(source, REMEvidenceType.class);

            REMEvidenceType remEvidence = jaxbRemEvidence.getValue();

            // Version
            if (!"2".equals(remEvidence.getVersion()))
                throw new RemEvidenceException(String.format("Version '%s' not known.", remEvidence.getVersion()));

            Evidence evidence = Evidence.newInstance();

            // Type
            evidence = evidence.type(EvidenceTypeInstance.findByLocalName(jaxbRemEvidence.getName().getLocalPart()));

            // Event Code
            evidence = evidence.eventCode(EventCode.valueFor(remEvidence.getEventCode()));

            // Event Reason
            evidence = evidence.eventReason(EventReason.valueForCode(remEvidence.getEventReasons()
                    .getEventReason().get(0).getCode()));

            // Issuer
            if (remEvidence.getEvidenceIssuerDetails() != null)
                evidence = evidence.issuer(remEvidence.getEvidenceIssuerDetails().getNamesPostalAddresses()
                        .getNamePostalAddress().get(0).getEntityName().getName().get(0));

            // Evidence Identifier
            evidence = evidence.evidenceIdentifier(InstanceIdentifier.of(remEvidence.getEvidenceIdentifier()));

            if (remEvidence.getEvidenceIssuerPolicyID() != null)
                evidence = evidence.issuerPolicy(remEvidence.getEvidenceIssuerPolicyID().getPolicyID().get(0));

            // Event Time
            evidence = evidence.timestamp(RemHelper.fromXmlGregorianCalendar(remEvidence.getEventTime()));

            // Sender
            evidence = evidence.sender(RemHelper.readElectronicAddressType((AttributedElectronicAddressType)
                    remEvidence.getSenderDetails().getAttributedElectronicAddressOrElectronicAddress().get(0)));

            // Receiver
            evidence = evidence.receiver(RemHelper.readElectronicAddressType((AttributedElectronicAddressType)
                    remEvidence.getRecipientsDetails().getEntityDetails().get(0)
                            .getAttributedElectronicAddressOrElectronicAddress().get(0)));

            // Sender Message Details
            evidence = evidence.digest(Digest.of(DigestMethod.fromUri(remEvidence.getSenderMessageDetails()
                    .getDigestMethod().getAlgorithm()), remEvidence.getSenderMessageDetails().getDigestValue()));
            if (remEvidence.getSenderMessageDetails().getUAMessageIdentifier() != null)
                evidence = evidence.documentIdentifier(InstanceIdentifier.of(
                        remEvidence.getSenderMessageDetails().getUAMessageIdentifier()));
            evidence = evidence.messageIdentifier(InstanceIdentifier.of(
                    remEvidence.getSenderMessageDetails().getMessageIdentifierByREMMD()));
            evidence = evidence.documentTypeIdentifier(DocumentTypeIdentifier.of(
                    remEvidence.getSenderMessageDetails().getMessageSubject(), Scheme.NONE));

            // Extensions
            if (remEvidence.getExtensions() != null) {

                // PEPPOL REM Extension
                for (ExtensionType extensionType : remEvidence.getExtensions().getExtension()) {
                    for (Object o : extensionType.getContent()) {
                        if (o instanceof PeppolRemExtension) {
                            PeppolRemExtension peppolRemExtension = (PeppolRemExtension) o;

                            evidence = evidence.transportProtocol(TransportProtocol.of(
                                    peppolRemExtension.getTransmissionProtocol()));
                            evidence = evidence.transmissionRole(peppolRemExtension.getTransmissionRole());

                            for (OriginalReceiptType receiptType : peppolRemExtension.getOriginalReceipt())
                                evidence = evidence.originalReceipt(
                                        Receipt.of(receiptType.getType(), receiptType.getValue()));
                        }
                    }
                }

            }

            return evidence;
        } catch (RemEvidenceException e) {
            throw e;
        } catch (JAXBException | PeppolException e) {
            throw new RemEvidenceException("Unable to unmarshal content.", e);
        }
    }
}
