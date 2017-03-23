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

import no.difi.vefa.peppol.common.api.SimpleIdentifier;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.evidence.jaxb.receipt.TransmissionRole;

import java.io.Serializable;
import java.util.*;

public class Evidence implements Serializable {

    private static final long serialVersionUID = 6577654274153420171L;

    private EvidenceTypeInstance type;

    private EventCode eventCode;

    private EventReason eventReason;

    private String issuer = "Unknown";

    private SimpleIdentifier evidenceIdentifier;

    private Date timestamp;

    private ParticipantIdentifier sender;

    private ParticipantIdentifier receiver;

    private DocumentTypeIdentifier documentTypeIdentifier;

    private SimpleIdentifier documentIdentifier;

    private String issuerPolicy;

    private Digest digest;

    private SimpleIdentifier messageIdentifier;

    private TransportProtocol transportProtocol;

    private TransmissionRole transmissionRole;

    private List<Receipt> originalReceipts = Collections.unmodifiableList(new ArrayList<Receipt>());

    public static Evidence newInstance() {
        return new Evidence();
    }

    private Evidence() {
    }

    private Evidence(EvidenceTypeInstance type, EventCode eventCode, EventReason eventReason, String issuer,
                     SimpleIdentifier evidenceIdentifier, Date timestamp, ParticipantIdentifier sender,
                     ParticipantIdentifier receiver, DocumentTypeIdentifier documentTypeIdentifier,
                     SimpleIdentifier documentIdentifier, String issuerPolicy, Digest digest,
                     SimpleIdentifier messageIdentifier, TransportProtocol transportProtocol,
                     TransmissionRole transmissionRole,
                     List<Receipt> originalReceipts) {
        this.type = type;
        this.eventCode = eventCode;
        this.eventReason = eventReason;
        this.issuer = issuer;
        this.evidenceIdentifier = evidenceIdentifier;
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
        this.documentTypeIdentifier = documentTypeIdentifier;
        this.documentIdentifier = documentIdentifier;
        this.issuerPolicy = issuerPolicy;
        this.digest = digest;
        this.messageIdentifier = messageIdentifier;
        this.transportProtocol = transportProtocol;
        this.transmissionRole = transmissionRole;
        this.originalReceipts = originalReceipts;
    }

    public EvidenceTypeInstance getType() {
        return type;
    }

    public Evidence type(EvidenceTypeInstance type) {
        Evidence evidence = copy();
        evidence.type = type;
        return evidence;
    }

    public EventCode getEventCode() {
        return eventCode;
    }

    public Evidence eventCode(EventCode eventCode) {
        Evidence evidence = copy();
        evidence.eventCode = eventCode;
        return evidence;
    }

    public EventReason getEventReason() {
        return eventReason;
    }

    public Evidence eventReason(EventReason eventReason) {
        Evidence evidence = copy();
        evidence.eventReason = eventReason;
        return evidence;
    }

    public String getIssuer() {
        return issuer;
    }

    public Evidence issuer(String issuer) {
        Evidence evidence = copy();
        evidence.issuer = issuer;
        return evidence;
    }

    public SimpleIdentifier getEvidenceIdentifier() {
        return evidenceIdentifier;
    }

    public Evidence evidenceIdentifier(SimpleIdentifier evidenceIdentifier) {
        Evidence evidence = copy();
        evidence.evidenceIdentifier = evidenceIdentifier;
        return evidence;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Evidence timestamp(Date timestamp) {
        if (timestamp != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timestamp);
            calendar.set(Calendar.MILLISECOND, 0);
            timestamp = calendar.getTime();
        }

        Evidence evidence = copy();
        evidence.timestamp = timestamp;
        return evidence;
    }

    public Evidence header(Header header) {
        Evidence evidence = copy();
        evidence.sender = header.getSender();
        evidence.receiver = header.getReceiver();
        evidence.documentTypeIdentifier = header.getDocumentType();
        evidence.documentIdentifier = header.getIdentifier();
        return evidence;
    }

    public ParticipantIdentifier getSender() {
        return sender;
    }

    public Evidence sender(ParticipantIdentifier sender) {
        Evidence evidence = copy();
        evidence.sender = sender;
        return evidence;
    }

    public ParticipantIdentifier getReceiver() {
        return receiver;
    }

    public Evidence receiver(ParticipantIdentifier receiver) {
        Evidence evidence = copy();
        evidence.receiver = receiver;
        return evidence;
    }

    public DocumentTypeIdentifier getDocumentTypeIdentifier() {
        return documentTypeIdentifier;
    }

    public Evidence documentTypeIdentifier(DocumentTypeIdentifier documentTypeIdentifier) {
        Evidence evidence = copy();
        evidence.documentTypeIdentifier = documentTypeIdentifier;
        return evidence;
    }

    public SimpleIdentifier getDocumentIdentifier() {
        return documentIdentifier;
    }

    public Evidence documentIdentifier(SimpleIdentifier documentIdentifier) {
        Evidence evidence = copy();
        evidence.documentIdentifier = documentIdentifier;
        return evidence;
    }

    public String getIssuerPolicy() {
        return issuerPolicy;
    }

    public Evidence issuerPolicy(String issuerPolicy) {
        Evidence evidence = copy();
        evidence.issuerPolicy = issuerPolicy;
        return evidence;
    }

    public Digest getDigest() {
        return digest;
    }

    public Evidence digest(Digest digest) {
        Evidence evidence = copy();
        evidence.digest = digest;
        return evidence;
    }

    public SimpleIdentifier getMessageIdentifier() {
        return messageIdentifier;
    }

    public Evidence messageIdentifier(SimpleIdentifier messageIdentifier) {
        Evidence evidence = copy();
        evidence.messageIdentifier = messageIdentifier;
        return evidence;
    }

    public TransportProtocol getTransportProtocol() {
        return transportProtocol;
    }

    public Evidence transportProtocol(TransportProtocol transportProtocol) {
        Evidence evidence = copy();
        evidence.transportProtocol = transportProtocol;
        return evidence;
    }

    public TransmissionRole getTransmissionRole() {
        return transmissionRole;
    }

    public Evidence transmissionRole(TransmissionRole transmissionRole) {
        Evidence evidence = copy();
        evidence.transmissionRole = transmissionRole;
        return evidence;
    }

    public List<Receipt> getOriginalReceipts() {
        return originalReceipts;
    }

    public Evidence originalReceipt(Receipt receipt) {
        return originalReceipts(Collections.singletonList(receipt));
    }

    public Evidence originalReceipts(List<Receipt> receipts) {
        List<Receipt> originalReceipts = new ArrayList<>(this.originalReceipts);

        for (Receipt receipt : receipts)
            if (receipt != null)
                originalReceipts.add(receipt);
        originalReceipts = Collections.unmodifiableList(originalReceipts);

        Evidence evidence = copy();
        evidence.originalReceipts = originalReceipts;
        return evidence;
    }

    protected boolean hasPeppolExtensionValues() {
        return (transmissionRole != null || transportProtocol != null || originalReceipts.size() > 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Evidence evidence = (Evidence) o;

        if (type != evidence.type) return false;
        if (eventCode != evidence.eventCode) return false;
        if (eventReason != evidence.eventReason) return false;
        if (issuer != null ? !issuer.equals(evidence.issuer) : evidence.issuer != null) return false;
        if (evidenceIdentifier != null ? !evidenceIdentifier.equals(evidence.evidenceIdentifier) :
                evidence.evidenceIdentifier != null)
            return false;
        if (timestamp != null ? !timestamp.equals(evidence.timestamp) : evidence.timestamp != null) return false;
        if (sender != null ? !sender.equals(evidence.sender) : evidence.sender != null) return false;
        if (receiver != null ? !receiver.equals(evidence.receiver) : evidence.receiver != null) return false;
        if (documentTypeIdentifier != null ? !documentTypeIdentifier.equals(evidence.documentTypeIdentifier) :
                evidence.documentTypeIdentifier != null)
            return false;
        if (documentIdentifier != null ? !documentIdentifier.equals(evidence.documentIdentifier) :
                evidence.documentIdentifier != null)
            return false;
        if (issuerPolicy != null ? !issuerPolicy.equals(evidence.issuerPolicy) : evidence.issuerPolicy != null)
            return false;
        if (digest != null ? !digest.equals(evidence.digest) : evidence.digest != null) return false;
        if (messageIdentifier != null ? !messageIdentifier.equals(evidence.messageIdentifier) :
                evidence.messageIdentifier != null)
            return false;
        if (transportProtocol != null ? !transportProtocol.equals(evidence.transportProtocol) :
                evidence.transportProtocol != null)
            return false;
        if (transmissionRole != evidence.transmissionRole) return false;
        return originalReceipts.equals(evidence.originalReceipts);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (eventCode != null ? eventCode.hashCode() : 0);
        result = 31 * result + (eventReason != null ? eventReason.hashCode() : 0);
        result = 31 * result + (issuer != null ? issuer.hashCode() : 0);
        result = 31 * result + (evidenceIdentifier != null ? evidenceIdentifier.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        result = 31 * result + (receiver != null ? receiver.hashCode() : 0);
        result = 31 * result + (documentTypeIdentifier != null ? documentTypeIdentifier.hashCode() : 0);
        result = 31 * result + (documentIdentifier != null ? documentIdentifier.hashCode() : 0);
        result = 31 * result + (issuerPolicy != null ? issuerPolicy.hashCode() : 0);
        result = 31 * result + (digest != null ? digest.hashCode() : 0);
        result = 31 * result + (messageIdentifier != null ? messageIdentifier.hashCode() : 0);
        result = 31 * result + (transportProtocol != null ? transportProtocol.hashCode() : 0);
        result = 31 * result + (transmissionRole != null ? transmissionRole.hashCode() : 0);
        result = 31 * result + originalReceipts.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Evidence{" +
                "type=" + type +
                ",\n eventCode=" + eventCode +
                ",\n eventReason=" + eventReason +
                ",\n issuer=" + issuer +
                ",\n evidenceIdentifier=" + evidenceIdentifier +
                ",\n timestamp=" + timestamp +
                ",\n sender=" + sender +
                ",\n receiver=" + receiver +
                ",\n documentTypeIdentifier=" + documentTypeIdentifier +
                ",\n documentIdentifier=" + documentIdentifier +
                ",\n issuerPolicy=" + issuerPolicy +
                ",\n digest=" + digest +
                ",\n messageIdentifier=" + messageIdentifier +
                ",\n transportProtocol=" + transportProtocol +
                ",\n transmissionRole=" + transmissionRole +
                ",\n originalReceipts=" + originalReceipts +
                '}';
    }

    private Evidence copy() {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver,
                documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier,
                transportProtocol, transmissionRole, originalReceipts);
    }
}
