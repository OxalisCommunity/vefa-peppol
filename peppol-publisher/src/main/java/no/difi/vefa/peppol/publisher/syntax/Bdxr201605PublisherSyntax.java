package no.difi.vefa.peppol.publisher.syntax;

import no.difi.commons.bdx.jaxb.smp._2016._05.*;
import no.difi.vefa.peppol.common.api.PerformAction;
import no.difi.vefa.peppol.common.model.*;
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
@Syntax({"bdxr", "bdxr-201605"})
public class Bdxr201605PublisherSyntax implements PublisherSyntax {

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
        serviceGroupType.setParticipantIdentifier(convert(serviceGroup.getParticipantIdentifier()));
        serviceGroupType.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType());

        for (DocumentTypeIdentifier documentTypeIdentifier : serviceGroup.getDocumentTypeIdentifiers())
            serviceGroupType
                    .getServiceMetadataReferenceCollection()
                    .getServiceMetadataReference()
                    .add(convertRef(serviceGroup.getParticipantIdentifier(), documentTypeIdentifier, rootUri));

        return OBJECT_FACTORY.createServiceGroup(serviceGroupType);
    }

    @Override
    public JAXBElement<?> of(ServiceMetadata serviceMetadata, boolean forSigning) {
        ServiceInformationType serviceInformationType = new ServiceInformationType();
        serviceInformationType.setParticipantIdentifier(convert(serviceMetadata.getParticipantIdentifier()));
        serviceInformationType.setDocumentIdentifier(convert(serviceMetadata.getDocumentTypeIdentifier()));
        serviceInformationType.setProcessList(new ProcessListType());

        for (ProcessMetadata processMetadata : serviceMetadata.getProcesses())
            serviceInformationType.getProcessList().getProcess().add(convert(processMetadata));

        ServiceMetadataType serviceMetadataType = new ServiceMetadataType();
        serviceMetadataType.setServiceInformation(serviceInformationType);

        if (forSigning) {
            SignedServiceMetadataType signedServiceMetadataType = new SignedServiceMetadataType();
            signedServiceMetadataType.setServiceMetadata(serviceMetadataType);
            return OBJECT_FACTORY.createSignedServiceMetadata(signedServiceMetadataType);
        } else {
            return OBJECT_FACTORY.createServiceMetadata(serviceMetadataType);
        }
    }

    @Override
    public Marshaller getMarshaller() throws JAXBException {
        return jaxbContext.createMarshaller();
    }

    private ParticipantIdentifierType convert(ParticipantIdentifier participantIdentifier) {
        ParticipantIdentifierType participantIdentifierType = new ParticipantIdentifierType();
        participantIdentifierType.setScheme(participantIdentifier.getScheme().getValue());
        participantIdentifierType.setValue(participantIdentifier.getIdentifier());
        return participantIdentifierType;
    }

    private ProcessIdentifierType convert(ProcessIdentifier processIdentifier) {
        ProcessIdentifierType processIdentifierType = new ProcessIdentifierType();
        processIdentifierType.setScheme(processIdentifier.getScheme().getValue());
        processIdentifierType.setValue(processIdentifier.getIdentifier());
        return processIdentifierType;
    }

    private DocumentIdentifierType convert(DocumentTypeIdentifier documentTypeIdentifier) {
        DocumentIdentifierType documentIdentifierType = new DocumentIdentifierType();
        documentIdentifierType.setScheme(documentTypeIdentifier.getScheme().getValue());
        documentIdentifierType.setValue(documentIdentifierType.getValue());
        return documentIdentifierType;
    }

    private ProcessType convert(ProcessMetadata processMetadata) {
        ProcessType processType = new ProcessType();
        processType.setProcessIdentifier(convert(processMetadata.getProcessIdentifier()));
        processType.setServiceEndpointList(new ServiceEndpointList());

        for (Endpoint endpoint : processMetadata.getEndpoints())
            processType.getServiceEndpointList().getEndpoint().add(convert(endpoint));

        return processType;
    }

    private EndpointType convert(Endpoint endpoint) {
        EndpointType endpointType = new EndpointType();
        endpointType.setTransportProfile(endpoint.getTransportProfile().getValue());
        endpointType.setRequireBusinessLevelSignature(false);
        endpointType.setEndpointURI(endpoint.getAddress().toString());
        // endpointType.setCertificate(endpoint.getCertificate().getEncoded());

        return endpointType;
    }

    private ServiceMetadataReferenceType convertRef(ParticipantIdentifier participantIdentifier,
                                                    DocumentTypeIdentifier documentTypeIdentifier, URI rootURI) {
        URI uri = rootURI.resolve(String.format("%s/services/%s",
                participantIdentifier.urlencoded(), documentTypeIdentifier.urlencoded()));

        ServiceMetadataReferenceType serviceMetadataReferenceType = new ServiceMetadataReferenceType();
        serviceMetadataReferenceType.setHref(uri.toString());
        return serviceMetadataReferenceType;
    }
}
