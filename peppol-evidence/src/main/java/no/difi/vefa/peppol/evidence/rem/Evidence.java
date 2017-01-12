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
import no.difi.vefa.peppol.evidence.jaxb.receipt.TransmissionRole;

import java.io.Serializable;
import java.util.*;

public class Evidence implements Serializable {

    private static final long serialVersionUID = 6577654274153420171L;

    private EvidenceTypeInstance type;

    private EventCode eventCode;

    private EventReason eventReason;

    private String issuer = "Unknown";

    private InstanceIdentifier evidenceIdentifier;

    private Date timestamp;

    private ParticipantIdentifier sender;

    private ParticipantIdentifier receiver;

    private DocumentTypeIdentifier documentTypeIdentifier;

    private InstanceIdentifier documentIdentifier;

    private String issuerPolicy;

    private Digest digest;

    private InstanceIdentifier messageIdentifier;

    private TransportProtocol transportProtocol;

    private TransmissionRole transmissionRole;

    private List<Receipt> originalReceipts = Collections.unmodifiableList(new ArrayList<Receipt>());

    public static Evidence newInstance() {
        return new Evidence();
    }

    private Evidence() {
    }

    private Evidence(EvidenceTypeInstance type, EventCode eventCode, EventReason eventReason, String issuer, InstanceIdentifier evidenceIdentifier, Date timestamp, ParticipantIdentifier sender,
                     ParticipantIdentifier receiver, DocumentTypeIdentifier documentTypeIdentifier, InstanceIdentifier documentIdentifier, String issuerPolicy, Digest digest,
                     InstanceIdentifier messageIdentifier, TransportProtocol transportProtocol, TransmissionRole transmissionRole,
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
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public EventCode getEventCode() {
        return eventCode;
    }

    public Evidence eventCode(EventCode eventCode) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public EventReason getEventReason() {
        return eventReason;
    }

    public Evidence eventReason(EventReason eventReason) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public String getIssuer() {
        return issuer;
    }

    public Evidence issuer(String issuer) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public InstanceIdentifier getEvidenceIdentifier() {
        return evidenceIdentifier;
    }

    public Evidence evidenceIdentifier(InstanceIdentifier evidenceIdentifier) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
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

        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public ParticipantIdentifier getSender() {
        return sender;
    }

    public Evidence sender(ParticipantIdentifier sender) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public ParticipantIdentifier getReceiver() {
        return receiver;
    }

    public Evidence receiver(ParticipantIdentifier receiver) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public DocumentTypeIdentifier getDocumentTypeIdentifier() {
        return documentTypeIdentifier;
    }

    public Evidence documentTypeIdentifier(DocumentTypeIdentifier documentTypeIdentifier) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public InstanceIdentifier getDocumentIdentifier() {
        return documentIdentifier;
    }

    public Evidence documentIdentifier(InstanceIdentifier documentIdentifier) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public String getIssuerPolicy() {
        return issuerPolicy;
    }

    public Evidence issuerPolicy(String issuerPolicy) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public Digest getDigest() {
        return digest;
    }

    public Evidence digest(Digest digest) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public InstanceIdentifier getMessageIdentifier() {
        return messageIdentifier;
    }

    public Evidence messageIdentifier(InstanceIdentifier messageIdentifier) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public TransportProtocol getTransportProtocol() {
        return transportProtocol;
    }

    public Evidence transportProtocol(TransportProtocol transportProtocol) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public TransmissionRole getTransmissionRole() {
        return transmissionRole;
    }

    public Evidence transmissionRole(TransmissionRole transmissionRole) {
        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    public List<Receipt> getOriginalReceipts() {
        return originalReceipts;
    }

    public Evidence originalReceipt(Receipt receipt) {
        if (receipt == null)
            return this;

        List<Receipt> originalReceipts = new ArrayList<>(this.originalReceipts);
        originalReceipts.add(receipt);
        originalReceipts = Collections.unmodifiableList(originalReceipts);

        return new Evidence(type, eventCode, eventReason, issuer, evidenceIdentifier, timestamp, sender, receiver, documentTypeIdentifier, documentIdentifier, issuerPolicy, digest, messageIdentifier, transportProtocol, transmissionRole, originalReceipts);
    }

    boolean hasPeppolExtensionValues() {
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
        if (evidenceIdentifier != null ? !evidenceIdentifier.equals(evidence.evidenceIdentifier) : evidence.evidenceIdentifier != null)
            return false;
        if (timestamp != null ? !timestamp.equals(evidence.timestamp) : evidence.timestamp != null) return false;
        if (sender != null ? !sender.equals(evidence.sender) : evidence.sender != null) return false;
        if (receiver != null ? !receiver.equals(evidence.receiver) : evidence.receiver != null) return false;
        if (documentTypeIdentifier != null ? !documentTypeIdentifier.equals(evidence.documentTypeIdentifier) : evidence.documentTypeIdentifier != null)
            return false;
        if (documentIdentifier != null ? !documentIdentifier.equals(evidence.documentIdentifier) : evidence.documentIdentifier != null)
            return false;
        if (issuerPolicy != null ? !issuerPolicy.equals(evidence.issuerPolicy) : evidence.issuerPolicy != null)
            return false;
        if (digest != null ? !digest.equals(evidence.digest) : evidence.digest != null) return false;
        if (messageIdentifier != null ? !messageIdentifier.equals(evidence.messageIdentifier) : evidence.messageIdentifier != null)
            return false;
        if (transportProtocol != null ? !transportProtocol.equals(evidence.transportProtocol) : evidence.transportProtocol != null)
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
}
