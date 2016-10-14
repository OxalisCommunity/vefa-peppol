package no.difi.vefa.peppol.lookup;

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.lookup.api.*;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.api.PeppolSecurityException;

import java.net.URI;
import java.util.List;

public class LookupClient {

    private MetadataLocator metadataLocator;

    private MetadataProvider metadataProvider;

    private MetadataFetcher metadataFetcher;

    private MetadataReader metadataReader;

    private CertificateValidator providerCertificateValidator;

    private CertificateValidator endpointCertificateValidator;

    LookupClient(MetadataLocator metadataLocator, MetadataProvider metadataProvider, MetadataFetcher metadataFetcher,
                 MetadataReader metadataReader, CertificateValidator providerCertificateValidator,
                 CertificateValidator endpointCertificateValidator) {
        this.metadataLocator = metadataLocator;
        this.metadataProvider = metadataProvider;
        this.metadataFetcher = metadataFetcher;
        this.metadataReader = metadataReader;
        this.providerCertificateValidator = providerCertificateValidator;
        this.endpointCertificateValidator = endpointCertificateValidator;
    }

    public List<DocumentTypeIdentifier> getDocumentIdentifiers(ParticipantIdentifier participantIdentifier)
            throws LookupException {
        URI location = metadataLocator.lookup(participantIdentifier);
        URI provider = metadataProvider.resolveDocumentIdentifiers(location, participantIdentifier);

        return metadataReader.parseDocumentIdentifiers(metadataFetcher.fetch(provider));
    }

    public ServiceMetadata getServiceMetadata(ParticipantIdentifier participantIdentifier,
                                              DocumentTypeIdentifier documentTypeIdentifier)
            throws LookupException, PeppolSecurityException {
        URI location = metadataLocator.lookup(participantIdentifier);
        URI provider = metadataProvider.resolveServiceMetadata(location, participantIdentifier, documentTypeIdentifier);

        ServiceMetadata serviceMetadata = metadataReader.parseServiceMetadata(metadataFetcher.fetch(provider));

        if (providerCertificateValidator != null)
            providerCertificateValidator.validate(serviceMetadata.getSigner());

        return serviceMetadata;
    }

    public Endpoint getEndpoint(ServiceMetadata serviceMetadata, ProcessIdentifier processIdentifier,
                                TransportProfile... transportProfiles)
            throws PeppolSecurityException, EndpointNotFoundException {
        Endpoint endpoint = serviceMetadata.getEndpoint(processIdentifier, transportProfiles);

        if (endpointCertificateValidator != null)
            endpointCertificateValidator.validate(endpoint.getCertificate());

        return endpoint;
    }

    public Endpoint getEndpoint(ParticipantIdentifier participantIdentifier,
                                DocumentTypeIdentifier documentTypeIdentifier, ProcessIdentifier processIdentifier,
                                TransportProfile... transportProfiles)
            throws LookupException, PeppolSecurityException, EndpointNotFoundException {
        ServiceMetadata serviceMetadata = getServiceMetadata(participantIdentifier, documentTypeIdentifier);
        return getEndpoint(serviceMetadata, processIdentifier, transportProfiles);
    }
}
