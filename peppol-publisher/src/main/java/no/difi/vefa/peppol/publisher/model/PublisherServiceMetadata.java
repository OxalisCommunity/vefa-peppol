package no.difi.vefa.peppol.publisher.model;

import no.difi.vefa.peppol.common.model.AbstractServiceMetadata;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessMetadata;

import java.util.List;

/**
 * @author erlend
 */
public class PublisherServiceMetadata extends AbstractServiceMetadata<PublisherEndpoint> {

    public PublisherServiceMetadata(ParticipantIdentifier participantIdentifier,
                                    DocumentTypeIdentifier documentTypeIdentifier,
                                    List<ProcessMetadata<PublisherEndpoint>> processes) {
        super(participantIdentifier, documentTypeIdentifier, processes);
    }
}
