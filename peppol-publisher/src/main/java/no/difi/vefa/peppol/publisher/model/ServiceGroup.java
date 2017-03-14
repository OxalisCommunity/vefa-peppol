package no.difi.vefa.peppol.publisher.model;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author erlend
 */
public class ServiceGroup implements Serializable {

    private static final long serialVersionUID = -4268277692087478476L;

    private ParticipantIdentifier participantIdentifier;

    private List<DocumentTypeIdentifier> documentTypeIdentifiers;

    public static ServiceGroup of(ParticipantIdentifier participantIdentifier,
                                  List<DocumentTypeIdentifier> documentTypeIdentifiers) {
        return new ServiceGroup(participantIdentifier, documentTypeIdentifiers);
    }

    private ServiceGroup(ParticipantIdentifier participantIdentifier,
                        List<DocumentTypeIdentifier> documentTypeIdentifiers) {
        this.participantIdentifier = participantIdentifier;
        this.documentTypeIdentifiers = documentTypeIdentifiers;
    }

    public ParticipantIdentifier getParticipantIdentifier() {
        return participantIdentifier;
    }

    public List<DocumentTypeIdentifier> getDocumentTypeIdentifiers() {
        return Collections.unmodifiableList(documentTypeIdentifiers);
    }
}
