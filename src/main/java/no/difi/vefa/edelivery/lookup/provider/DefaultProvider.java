package no.difi.vefa.edelivery.lookup.provider;

import no.difi.vefa.edelivery.lookup.api.MetadataProvider;
import no.difi.vefa.edelivery.lookup.model.DocumentIdentifier;
import no.difi.vefa.edelivery.lookup.model.ParticipantIdentifier;

import java.net.URI;

public class DefaultProvider implements MetadataProvider {

    @Override
    public URI resolveDocumentIdentifiers(URI location, ParticipantIdentifier participantIdentifier) {
        return location.resolve(String.format("/%s", participantIdentifier.urlencoded()));
    }

    @Override
    public URI resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier, DocumentIdentifier documentIdentifier) {
        return location.resolve(String.format("/%s/services/%s", participantIdentifier.urlencoded(), documentIdentifier.urlencoded()));
    }

}
