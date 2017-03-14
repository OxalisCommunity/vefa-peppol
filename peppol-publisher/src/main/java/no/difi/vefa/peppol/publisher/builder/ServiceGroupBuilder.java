package no.difi.vefa.peppol.publisher.builder;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.publisher.model.ServiceGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author erlend
 */
public class ServiceGroupBuilder {

    private ParticipantIdentifier participantIdentifier;

    private List<DocumentTypeIdentifier> documentTypeIdentifiers = new ArrayList<>();

    public static ServiceGroupBuilder newInstance(ParticipantIdentifier participantIdentifier) {
        return new ServiceGroupBuilder(participantIdentifier);
    }

    private ServiceGroupBuilder(ParticipantIdentifier participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public ServiceGroupBuilder add(DocumentTypeIdentifier documentTypeIdentifier) {
        this.documentTypeIdentifiers.add(documentTypeIdentifier);
        return this;
    }

    public ServiceGroup build() {
        return ServiceGroup.of(participantIdentifier, documentTypeIdentifiers);
    }
}
