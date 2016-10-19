/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
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

package no.difi.vefa.peppol.common.model;

import java.io.Serializable;
import java.util.Date;

public class Header implements Serializable {

    private ParticipantIdentifier sender;

    private ParticipantIdentifier receiver;

    private ProcessIdentifier process;

    private DocumentTypeIdentifier documentType;

    private InstanceIdentifier identifier;

    private InstanceType instanceType;

    private Date creationTimestamp;

    public static Header newInstance() {
        return new Header();
    }

    public static Header of(ParticipantIdentifier sender, ParticipantIdentifier receiver, ProcessIdentifier process,
                            DocumentTypeIdentifier documentType, InstanceIdentifier identifier,
                            InstanceType instanceType, Date creationTimestamp) {
        return new Header(sender, receiver, process, documentType, identifier, instanceType, creationTimestamp);
    }

    public static Header of(ParticipantIdentifier sender, ParticipantIdentifier receiver, ProcessIdentifier process,
                            DocumentTypeIdentifier documentType) {
        return new Header(sender, receiver, process, documentType, null, null, null);
    }

    private Header() {
        // No action.
    }

    private Header(ParticipantIdentifier sender, ParticipantIdentifier receiver, ProcessIdentifier process,
                   DocumentTypeIdentifier documentType, InstanceIdentifier identifier, InstanceType instanceType,
                   Date creationTimestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.process = process;
        this.documentType = documentType;
        this.identifier = identifier;
        this.instanceType = instanceType;
        this.creationTimestamp = creationTimestamp;
    }

    public ParticipantIdentifier getSender() {
        return sender;
    }

    public Header sender(ParticipantIdentifier sender) {
        return new Header(sender, receiver, process, documentType, identifier, instanceType, creationTimestamp);
    }

    public ParticipantIdentifier getReceiver() {
        return receiver;
    }

    public Header receiver(ParticipantIdentifier receiver) {
        return new Header(sender, receiver, process, documentType, identifier, instanceType, creationTimestamp);
    }

    public ProcessIdentifier getProcess() {
        return process;
    }

    public Header process(ProcessIdentifier process) {
        return new Header(sender, receiver, process, documentType, identifier, instanceType, creationTimestamp);
    }

    public DocumentTypeIdentifier getDocumentType() {
        return documentType;
    }

    public Header documentType(DocumentTypeIdentifier documentType) {
        return new Header(sender, receiver, process, documentType, identifier, instanceType, creationTimestamp);
    }

    public InstanceIdentifier getIdentifier() {
        return identifier;
    }

    public Header identifier(InstanceIdentifier identifier) {
        return new Header(sender, receiver, process, documentType, identifier, instanceType, creationTimestamp);
    }

    public InstanceType getInstanceType() {
        return instanceType;
    }

    public Header instanceType(InstanceType instanceType) {
        return new Header(sender, receiver, process, documentType, identifier, instanceType, creationTimestamp);
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public Header creationTimestamp(Date creationTimestamp) {
        return new Header(sender, receiver, process, documentType, identifier, instanceType, creationTimestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        if (!sender.equals(header.sender)) return false;
        if (!receiver.equals(header.receiver)) return false;
        if (!process.equals(header.process)) return false;
        if (!documentType.equals(header.documentType)) return false;
        if (identifier != null ? !identifier.equals(header.identifier) : header.identifier != null) return false;
        if (instanceType != null ? !instanceType.equals(header.instanceType) : header.instanceType != null)
            return false;
        return !(creationTimestamp != null ?
                !creationTimestamp.equals(header.creationTimestamp) : header.creationTimestamp != null);
    }

    @Override
    public int hashCode() {
        int result = sender.hashCode();
        result = 31 * result + receiver.hashCode();
        result = 31 * result + process.hashCode();
        result = 31 * result + documentType.hashCode();
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        result = 31 * result + (instanceType != null ? instanceType.hashCode() : 0);
        result = 31 * result + (creationTimestamp != null ? creationTimestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Header{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", process=" + process +
                ", documentType=" + documentType +
                ", identifier=" + identifier +
                ", instanceType=" + instanceType +
                ", creationTimestamp=" + creationTimestamp +
                '}';
    }
}
