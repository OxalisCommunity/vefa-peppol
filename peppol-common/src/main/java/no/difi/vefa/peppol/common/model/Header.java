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

package no.difi.vefa.peppol.common.model;

import java.io.Serializable;
import java.util.Date;

public class Header implements Serializable {

    private static final long serialVersionUID = -7517561747468194479L;

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
        return new Header(sender, receiver, process, documentType, identifier,
                instanceType, creationTimestamp);
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
        Header header = copy();
        header.sender = sender;
        return header;
    }

    public ParticipantIdentifier getReceiver() {
        return receiver;
    }

    public Header receiver(ParticipantIdentifier receiver) {
        Header header = copy();
        header.receiver = receiver;
        return header;
    }

    public ProcessIdentifier getProcess() {
        return process;
    }

    public Header process(ProcessIdentifier process) {
        Header header = copy();
        header.process = process;
        return header;
    }

    public DocumentTypeIdentifier getDocumentType() {
        return documentType;
    }

    public Header documentType(DocumentTypeIdentifier documentType) {
        Header header = copy();
        header.documentType = documentType;
        return header;
    }

    public InstanceIdentifier getIdentifier() {
        return identifier;
    }

    public Header identifier(InstanceIdentifier identifier) {
        Header header = copy();
        header.identifier = identifier;
        return header;
    }

    public InstanceType getInstanceType() {
        return instanceType;
    }

    public Header instanceType(InstanceType instanceType) {
        Header header = copy();
        header.instanceType = instanceType;
        return header;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public Header creationTimestamp(Date creationTimestamp) {
        Header header = copy();
        header.creationTimestamp = creationTimestamp;
        return header;
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

    private Header copy() {
        return new Header(sender, receiver, process, documentType, identifier,
                instanceType, creationTimestamp);
    }
}
