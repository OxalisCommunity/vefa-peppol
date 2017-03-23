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

import no.difi.vefa.peppol.common.api.PerformAction;
import no.difi.vefa.peppol.common.model.Receipt;
import no.difi.vefa.peppol.common.util.ExceptionUtil;
import no.difi.vefa.peppol.evidence.jaxb.receipt.OriginalReceiptType;
import no.difi.vefa.peppol.evidence.jaxb.receipt.PeppolRemExtension;
import no.difi.vefa.peppol.evidence.jaxb.rem.*;
import no.difi.vefa.peppol.evidence.jaxb.xmldsig.DigestMethodType;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import org.w3c.dom.Node;

import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.math.BigInteger;

public class EvidenceWriter {

    private Evidence evidence;

    private REMEvidenceType remEvidence = new REMEvidenceType();

    public static void write(OutputStream outputStream, Evidence evidence) throws RemEvidenceException {
        EvidenceWriter evidenceWriter = new EvidenceWriter(evidence);
        evidenceWriter.prepare();
        evidenceWriter.write(new StreamResult(outputStream));
    }

    public static void write(Node node, Evidence evidence) throws RemEvidenceException {
        EvidenceWriter evidenceWriter = new EvidenceWriter(evidence);
        evidenceWriter.prepare();
        evidenceWriter.write(new DOMResult(node));
    }

    private EvidenceWriter(Evidence evidence) {
        this.evidence = evidence;
    }

    private void prepare() throws RemEvidenceException {
        // Version
        remEvidence.setVersion("2");

        // Event Code
        remEvidence.setEventCode(evidence.getEventCode().getValue());

        // Event Reason
        if (evidence.getEventReason() != null) {
            remEvidence.setEventReasons(new EventReasonsType());
            remEvidence.getEventReasons().getEventReason().add(
                    RemHelper.createEventReasonType(evidence.getEventReason()));
        }

        // Issuer
        NamePostalAddressType namePostalAddressType = new NamePostalAddressType();
        namePostalAddressType.setEntityName(new EntityNameType());
        namePostalAddressType.getEntityName().getName().add(evidence.getIssuer());

        remEvidence.setEvidenceIssuerDetails(new EntityDetailsType());
        remEvidence.getEvidenceIssuerDetails().setNamesPostalAddresses(new NamesPostalAddressListType());
        remEvidence.getEvidenceIssuerDetails().getNamesPostalAddresses()
                .getNamePostalAddress().add(namePostalAddressType);

        // Evidence Identifier
        remEvidence.setEvidenceIdentifier(evidence.getEvidenceIdentifier().getIdentifier());

        // Issuer Policy
        if (evidence.getIssuerPolicy() != null) {
            remEvidence.setEvidenceIssuerPolicyID(new EvidenceIssuerPolicyIDType());
            remEvidence.getEvidenceIssuerPolicyID().getPolicyID().add(evidence.getIssuerPolicy());
        }

        // Event Time
        remEvidence.setEventTime(RemHelper.toXmlGregorianCalendar(evidence.getTimestamp()));

        // Sender
        remEvidence.setSenderDetails(new EntityDetailsType());
        remEvidence.getSenderDetails().getAttributedElectronicAddressOrElectronicAddress().add(
                RemHelper.createElectronicAddressType(evidence.getSender()));

        // Receiver
        remEvidence.setRecipientsDetails(new EntityDetailsListType());
        remEvidence.getRecipientsDetails().getEntityDetails().add(new EntityDetailsType());
        remEvidence.getRecipientsDetails().getEntityDetails().get(0)
                .getAttributedElectronicAddressOrElectronicAddress()
                .add(RemHelper.createElectronicAddressType(evidence.getReceiver()));
        remEvidence.setEvidenceRefersToRecipient(BigInteger.valueOf(1));

        // Sender Message Details
        MessageDetailsType messageDetailsType = new MessageDetailsType();
        messageDetailsType.setMessageSubject(evidence.getDocumentTypeIdentifier().getIdentifier());
        if (evidence.getDocumentIdentifier() != null)
            messageDetailsType.setUAMessageIdentifier(evidence.getDocumentIdentifier().getIdentifier());
        messageDetailsType.setMessageIdentifierByREMMD(evidence.getMessageIdentifier().getIdentifier());
        DigestMethodType digestMethodType = new DigestMethodType();
        digestMethodType.setAlgorithm(evidence.getDigest().getMethod().getUri());
        messageDetailsType.setDigestMethod(digestMethodType);
        messageDetailsType.setDigestValue(evidence.getDigest().getValue());
        messageDetailsType.setIsNotification(false);
        remEvidence.setSenderMessageDetails(messageDetailsType);

        // Extensions
        ExtensionsListType extensionsListType = new ExtensionsListType();

        // PEPPOL REM Extension
        if (evidence.hasPeppolExtensionValues()) {
            PeppolRemExtension peppolRemExtension = new PeppolRemExtension();
            peppolRemExtension.setTransmissionProtocol(evidence.getTransportProtocol().getIdentifier());
            peppolRemExtension.setTransmissionRole(evidence.getTransmissionRole());

            for (Receipt receipt : evidence.getOriginalReceipts())
                peppolRemExtension.getOriginalReceipt().add(convert(receipt));

            ExtensionType extensionType = new ExtensionType();
            extensionType.getContent().add(peppolRemExtension);
            extensionsListType.getExtension().add(extensionType);
        }

        if (extensionsListType.getExtension().size() > 0)
            remEvidence.setExtensions(extensionsListType);
    }

    private OriginalReceiptType convert(Receipt receipt) {
        OriginalReceiptType originalReceiptType = new OriginalReceiptType();
        originalReceiptType.setType(receipt.getType());
        originalReceiptType.setValue(receipt.getValue());
        return originalReceiptType;
    }

    private void write(final Result result) throws RemEvidenceException {
        ExceptionUtil.perform(RemEvidenceException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                Marshaller marshaller = RemHelper.getMarshaller();
                marshaller.marshal(evidence.getType().toJAXBElement(remEvidence), result);
            }
        });
    }
}
