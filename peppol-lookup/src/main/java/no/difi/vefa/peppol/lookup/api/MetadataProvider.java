package no.difi.vefa.peppol.lookup.api;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

import java.net.URI;

public interface MetadataProvider {
    URI resolveDocumentIdentifiers(URI location, ParticipantIdentifier participantIdentifier);

    URI resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier);
}
