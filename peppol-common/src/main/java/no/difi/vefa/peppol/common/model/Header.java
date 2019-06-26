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

import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

@Getter
public class Header implements Serializable {

    private static final long serialVersionUID = -7517561747468194479L;

    private ParticipantIdentifier sender;

    private ParticipantIdentifier receiver;

    private List<ParticipantIdentifier> copyReceiver = new ArrayList<>();

    private ProcessIdentifier process;

    private DocumentTypeIdentifier documentType;

    private InstanceIdentifier identifier;

    private InstanceType instanceType;

    private Date creationTimestamp;

    private Map<String, ArgumentIdentifier> arguments = new HashMap<>();

    public static Header newInstance() {
        return new Header();
    }

    public static Header of(ParticipantIdentifier sender, ParticipantIdentifier receiver, List<ParticipantIdentifier> cc,
                            ProcessIdentifier process, DocumentTypeIdentifier documentType, InstanceIdentifier identifier,
                            InstanceType instanceType, Date creationTimestamp) {
        return new Header(sender, receiver, cc, process, documentType, identifier, instanceType, creationTimestamp, null);
    }

    public static Header of(ParticipantIdentifier sender, ParticipantIdentifier receiver,
                            ProcessIdentifier process, DocumentTypeIdentifier documentType, InstanceIdentifier identifier,
                            InstanceType instanceType, Date creationTimestamp) {
        return new Header(sender, receiver, new ArrayList<>(), process, documentType, identifier, instanceType, creationTimestamp, null);
    }

    public static Header of(ParticipantIdentifier sender, ParticipantIdentifier receiver, ProcessIdentifier process,
                            DocumentTypeIdentifier documentType) {
        return new Header(sender, receiver, new ArrayList<>(), process, documentType, null, null, null, null);
    }

    public Header() {
        // No action.
    }

    private Header(ParticipantIdentifier sender, ParticipantIdentifier receiver, List<ParticipantIdentifier> copyReceiver,
                   ProcessIdentifier process, DocumentTypeIdentifier documentType, InstanceIdentifier identifier,
                   InstanceType instanceType, Date creationTimestamp, Map<String, ArgumentIdentifier> arguments) {
        this.sender = sender;
        this.receiver = receiver;
        this.copyReceiver = copyReceiver;
        this.process = process;
        this.documentType = documentType;
        this.identifier = identifier;
        this.instanceType = instanceType;
        this.creationTimestamp = creationTimestamp;
        this.arguments = arguments == null ? this.arguments : arguments;
    }

    public Header sender(ParticipantIdentifier sender) {
        return copy(h -> h.sender = sender);
    }

    public Header receiver(ParticipantIdentifier receiver) {
        return copy(h -> h.receiver = receiver);
    }

    public Header cc(ParticipantIdentifier cc) {
        return copy(h -> h.copyReceiver.add(cc));
    }

    public Header process(ProcessIdentifier process) {
        return copy(h -> h.process = process);
    }

    public Header documentType(DocumentTypeIdentifier documentType) {
        return copy(h -> h.documentType = documentType);
    }

    public Header identifier(InstanceIdentifier identifier) {
        return copy(h -> h.identifier = identifier);
    }

    public Header instanceType(InstanceType instanceType) {
        return copy(h -> h.instanceType = instanceType);
    }

    public Header creationTimestamp(Date creationTimestamp) {
        return copy(h -> h.creationTimestamp = creationTimestamp);
    }

    public Header argument(ArgumentIdentifier identifier) {
        return copy(h -> h.arguments.put(identifier.getKey(), identifier));
    }

    public Header arguments(List<ArgumentIdentifier> extras) {
        return copy(h -> extras.forEach(ai -> h.arguments.put(ai.getKey(), ai)));
    }

    public ArgumentIdentifier getArgument(String key) {
        return arguments.get(key);
    }

    public List<ArgumentIdentifier> getArguments() {
        return new ArrayList<>(arguments.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Header header = (Header) o;
        return Objects.equals(sender, header.sender) &&
                Objects.equals(receiver, header.receiver) &&
                Objects.equals(process, header.process) &&
                Objects.equals(documentType, header.documentType) &&
                Objects.equals(identifier, header.identifier) &&
                Objects.equals(instanceType, header.instanceType) &&
                Objects.equals(creationTimestamp, header.creationTimestamp) &&
                Objects.equals(arguments, header.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, process, documentType, identifier, instanceType, creationTimestamp, arguments);
    }

    @Override
    public String toString() {
        return "Header{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", copyReceiver=" + copyReceiver +
                ", process=" + process +
                ", documentType=" + documentType +
                ", identifier=" + identifier +
                ", instanceType=" + instanceType +
                ", creationTimestamp=" + creationTimestamp +
                ", arguments=" + arguments +
                '}';
    }

    private Header copy(Consumer<Header> consumer) {
        Header header = new Header(sender, receiver, new ArrayList<>(copyReceiver), process, documentType, identifier,
                instanceType, creationTimestamp, new HashMap<>(arguments));
        consumer.accept(header);
        return header;
    }
}
