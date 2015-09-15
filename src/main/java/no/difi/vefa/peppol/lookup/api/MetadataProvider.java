package no.difi.vefa.peppol.lookup.api;

import no.difi.vefa.peppol.lookup.model.DocumentIdentifier;
import no.difi.vefa.peppol.lookup.model.ParticipantIdentifier;

import java.net.URI;

public interface MetadataProvider {
    URI resolveDocumentIdentifiers(URI location, ParticipantIdentifier participantIdentifier);

    URI resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier, DocumentIdentifier documentIdentifier);
}
