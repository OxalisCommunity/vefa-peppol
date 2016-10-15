package no.difi.vefa.peppol.lookup.provider;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.MetadataProvider;

import java.net.URI;

public class DefaultProvider implements MetadataProvider {

    @Override
    public URI resolveDocumentIdentifiers(URI location, ParticipantIdentifier participant) {
        return location.resolve(String.format("/%s",
                        participant.urlencoded())
        );
    }

    @Override
    public URI resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier,
                                      DocumentTypeIdentifier documentTypeIdentifier) {
        return location.resolve(String.format("/%s/services/%s",
                        participantIdentifier.urlencoded(),
                        documentTypeIdentifier.urlencoded())
        );
    }
}
