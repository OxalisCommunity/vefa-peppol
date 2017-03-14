package no.difi.vefa.peppol.publisher.syntax;

import no.difi.commons.busdox.jaxb.smp.ObjectFactory;
import no.difi.commons.busdox.jaxb.smp.ServiceGroupType;
import no.difi.commons.busdox.jaxb.smp.ServiceMetadataType;
import no.difi.commons.busdox.jaxb.smp.SignedServiceMetadataType;
import no.difi.vefa.peppol.common.api.PerformAction;
import no.difi.vefa.peppol.common.model.ServiceMetadata;
import no.difi.vefa.peppol.common.util.ExceptionUtil;
import no.difi.vefa.peppol.publisher.annotation.Syntax;
import no.difi.vefa.peppol.publisher.api.PublisherSyntax;
import no.difi.vefa.peppol.publisher.model.ServiceGroup;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.net.URI;

/**
 * @author erlend
 */
@Syntax("busdox")
public class BusdoxPublisherSyntax implements PublisherSyntax {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static JAXBContext jaxbContext;

    static {
        ExceptionUtil.perform(IllegalStateException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                jaxbContext = JAXBContext.newInstance(ServiceGroupType.class,
                        ServiceMetadataType.class, SignedServiceMetadataType.class);
            }
        });
    }

    @Override
    public JAXBElement<?> of(ServiceGroup serviceGroup, URI rootUri) {
        ServiceGroupType serviceGroupType = new ServiceGroupType();

        return OBJECT_FACTORY.createServiceGroup(serviceGroupType);
    }

    @Override
    public JAXBElement<?> of(ServiceMetadata serviceMetadata, boolean forSigning) {
        return null;
    }

    @Override
    public Marshaller getMarshaller() throws JAXBException {
        return jaxbContext.createMarshaller();
    }
}
