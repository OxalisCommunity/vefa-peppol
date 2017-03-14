package no.difi.vefa.peppol.publisher.api;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ServiceMetadata;
import no.difi.vefa.peppol.publisher.lang.PublisherException;

/**
 * @author erlend
 */
public interface ServiceMetadataProvider {

    ServiceMetadata get(ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier)
            throws PublisherException;

}
