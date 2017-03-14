package no.difi.vefa.peppol.publisher.builder;

import no.difi.vefa.peppol.common.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author erlend
 */
public class ServiceMetadataBuilder {

    private ParticipantIdentifier participantIdentifier;

    private DocumentTypeIdentifier documentTypeIdentifier;

    private List<ProcessMetadata> processes = new ArrayList<>();

    public ServiceMetadataBuilder participant(ParticipantIdentifier participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
        return this;
    }

    public ServiceMetadataBuilder documentTypeIdentifier(DocumentTypeIdentifier documentTypeIdentifier) {
        this.documentTypeIdentifier = documentTypeIdentifier;
        return this;
    }

    public ServiceMetadataBuilder add(ProcessIdentifier processIdentifier, Endpoint... endpoint) {
        this.processes.add(ProcessMetadata.of(processIdentifier, endpoint));
        return this;
    }

    public ServiceMetadata build() {
        return ServiceMetadata.of(participantIdentifier, documentTypeIdentifier, processes, null);
    }
}
