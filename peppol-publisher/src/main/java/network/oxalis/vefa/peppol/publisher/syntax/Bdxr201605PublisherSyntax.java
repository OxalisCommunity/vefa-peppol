/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package network.oxalis.vefa.peppol.publisher.syntax;

import network.oxalis.vefa.peppol.common.model.*;
import network.oxalis.vefa.peppol.common.util.ExceptionUtil;
import network.oxalis.vefa.peppol.publisher.annotation.Syntax;
import network.oxalis.vefa.peppol.publisher.model.PublisherEndpoint;
import network.oxalis.vefa.peppol.publisher.model.PublisherServiceMetadata;
import network.oxalis.vefa.peppol.publisher.model.ServiceGroup;
import network.oxalis.peppol.bdx.jaxb.smp._2016._05.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author erlend
 */
@Syntax({"bdxr", "bdxr-201605"})
public class Bdxr201605PublisherSyntax extends AbstractPublisherSyntax {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static final JAXBContext JAXB_CONTEXT =
            ExceptionUtil.perform(IllegalStateException.class, () ->
                    JAXBContext.newInstance(ServiceGroupType.class,
                            ServiceMetadataType.class, SignedServiceMetadataType.class));

    @SuppressWarnings("all")
    @Override
    public JAXBElement<?> of(ServiceGroup serviceGroup, URI rootUri) {
        ServiceGroupType serviceGroupType = new ServiceGroupType();
        serviceGroupType.setParticipantIdentifier(convert(serviceGroup.getParticipantIdentifier()));
        serviceGroupType.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType());

        for (ServiceReference serviceReference : serviceGroup.getServiceReferences())
            serviceGroupType
                    .getServiceMetadataReferenceCollection()
                    .getServiceMetadataReference()
                    .add(convertRef(serviceGroup.getParticipantIdentifier(), serviceReference.getDocumentTypeIdentifier(), rootUri));

        return OBJECT_FACTORY.createServiceGroup(serviceGroupType);
    }

    @SuppressWarnings("all")
    @Override
    public JAXBElement<?> of(PublisherServiceMetadata serviceMetadata, boolean forSigning) {
        ServiceMetadataType serviceMetadataType = new ServiceMetadataType();

        ServiceInformation<PublisherEndpoint> serviceInformation = serviceMetadata.getServiceInformation();

        if (serviceInformation != null) {
            ServiceInformationType serviceInformationType = new ServiceInformationType();
            serviceInformationType.setParticipantIdentifier(convert(serviceInformation.getParticipantIdentifier()));
            serviceInformationType.setDocumentIdentifier(convert(serviceInformation.getDocumentTypeIdentifier()));
            serviceInformationType.setProcessList(new ProcessListType());

            for (ProcessMetadata processMetadata : serviceInformation.getProcesses())
                serviceInformationType.getProcessList().getProcess().addAll(convert(processMetadata));

            serviceMetadataType.setServiceInformation(serviceInformationType);
        }

        Redirect redirect = serviceMetadata.getRedirect();

        if (redirect != null) {
            RedirectType redirectType = OBJECT_FACTORY.createRedirectType();
            redirectType.setCertificateUID(redirect.getCertificateUID());
            redirectType.setHref(redirect.getHref());
            serviceMetadataType.setRedirect(redirectType);
        }

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
        return JAXB_CONTEXT.createMarshaller();
    }

    private ParticipantIdentifierType convert(ParticipantIdentifier participantIdentifier) {
        ParticipantIdentifierType participantIdentifierType = new ParticipantIdentifierType();
        participantIdentifierType.setScheme(participantIdentifier.getScheme().getIdentifier());
        participantIdentifierType.setValue(participantIdentifier.getIdentifier());
        return participantIdentifierType;
    }

    private ProcessIdentifierType convert(ProcessIdentifier processIdentifier) {
        ProcessIdentifierType processIdentifierType = new ProcessIdentifierType();
        processIdentifierType.setScheme(processIdentifier.getScheme().getIdentifier());
        processIdentifierType.setValue(processIdentifier.getIdentifier());
        return processIdentifierType;
    }

    private DocumentIdentifierType convert(DocumentTypeIdentifier documentTypeIdentifier) {
        DocumentIdentifierType documentIdentifierType = new DocumentIdentifierType();
        documentIdentifierType.setScheme(documentTypeIdentifier.getScheme().getIdentifier());
        documentIdentifierType.setValue(documentTypeIdentifier.getIdentifier());
        return documentIdentifierType;
    }

    @SuppressWarnings("all")
    private List<ProcessType> convert(ProcessMetadata<PublisherEndpoint> processMetadata) {
        List<ProcessType> result = new ArrayList<>();

        for (ProcessIdentifier processIdentifier : processMetadata.getProcessIdentifier()) {
            ProcessType processType = new ProcessType();
            processType.setProcessIdentifier(convert(processIdentifier));
            processType.setServiceEndpointList(new ServiceEndpointList());

            for (PublisherEndpoint endpoint : processMetadata.getEndpoints())
                processType.getServiceEndpointList().getEndpoint().add(convert(endpoint));

            result.add(processType);
        }

        return result;
    }

    private EndpointType convert(PublisherEndpoint endpoint) {
        EndpointType endpointType = new EndpointType();
        endpointType.setTransportProfile(endpoint.getTransportProfile().getIdentifier());
        endpointType.setRequireBusinessLevelSignature(false);
        endpointType.setEndpointURI(endpoint.getAddress().toString());
        if (endpoint.getPeriod() != null) {
            endpointType.setServiceActivationDate(convert(endpoint.getPeriod().getFrom()));
            endpointType.setServiceExpirationDate(convert(endpoint.getPeriod().getTo()));
        }
        endpointType.setCertificate(endpoint.getCertificate());
        endpointType.setServiceDescription(endpoint.getDescription());
        endpointType.setTechnicalContactUrl(endpoint.getTechnicalContact());

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
