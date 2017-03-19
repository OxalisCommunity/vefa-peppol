package no.difi.vefa.peppol.publisher.api;

import no.difi.vefa.peppol.publisher.model.PublisherServiceMetadata;
import no.difi.vefa.peppol.publisher.model.ServiceGroup;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.net.URI;

/**
 * @author erlend
 */
public interface PublisherSyntax {

    JAXBElement<?> of(ServiceGroup serviceGroup, URI rootUri);

    JAXBElement<?> of(PublisherServiceMetadata serviceMetadata, boolean forSigning);

    Marshaller getMarshaller() throws JAXBException;

}
