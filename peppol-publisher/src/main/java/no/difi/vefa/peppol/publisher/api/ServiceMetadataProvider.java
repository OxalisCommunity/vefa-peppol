package no.difi.vefa.peppol.publisher.api;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ServiceMetadata;
import no.difi.vefa.peppol.publisher.lang.PublisherException;
import no.difi.vefa.peppol.publisher.model.PublisherServiceMetadata;

/**
 * @author erlend
 */
public interface ServiceMetadataProvider {

    PublisherServiceMetadata get(ParticipantIdentifier participantIdentifier,
                                 DocumentTypeIdentifier documentTypeIdentifier) throws PublisherException;

}
