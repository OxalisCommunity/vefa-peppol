package no.difi.vefa.peppol.lookup.provider;

import no.difi.vefa.peppol.lookup.api.MetadataProvider;
import no.difi.vefa.peppol.lookup.model.DocumentIdentifier;
import no.difi.vefa.peppol.lookup.model.ParticipantIdentifier;

import java.net.URI;

public class DefaultProvider implements MetadataProvider {

    @Override
    public URI resolveDocumentIdentifiers(URI location, ParticipantIdentifier participantIdentifier) {
        return location.resolve(String.format("/%s::%s", participantIdentifier.getScheme(), participantIdentifier.getIdentifier()));
    }

    @Override
    public URI resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier, DocumentIdentifier documentIdentifier) {
        return location.resolve(String.format("/%s/services/%s", participantIdentifier.urlencoded(), documentIdentifier.urlencoded()));
    }

}
