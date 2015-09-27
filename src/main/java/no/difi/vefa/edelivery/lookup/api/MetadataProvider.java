package no.difi.vefa.edelivery.lookup.api;

import no.difi.vefa.edelivery.lookup.model.DocumentIdentifier;
import no.difi.vefa.edelivery.lookup.model.ParticipantIdentifier;

import java.net.URI;

public interface MetadataProvider {
    URI resolveDocumentIdentifiers(URI location, ParticipantIdentifier participantIdentifier);

    URI resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier, DocumentIdentifier documentIdentifier);
}
