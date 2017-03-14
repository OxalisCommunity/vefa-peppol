package no.difi.vefa.peppol.publisher.api;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.publisher.lang.PublisherException;
import no.difi.vefa.peppol.publisher.model.ServiceGroup;

/**
 * @author erlend
 */
public interface ServiceGroupProvider {

    ServiceGroup get(ParticipantIdentifier participantIdentifier) throws PublisherException;

}
