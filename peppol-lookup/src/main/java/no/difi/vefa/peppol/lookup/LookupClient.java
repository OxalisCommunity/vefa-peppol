/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.lookup;

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.lookup.api.*;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;

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

        providerCertificateValidator.validate(serviceMetadata.getSigner());

        return serviceMetadata;
    }

    public Endpoint getEndpoint(ServiceMetadata serviceMetadata, ProcessIdentifier processIdentifier,
                                TransportProfile... transportProfiles)
            throws PeppolSecurityException, EndpointNotFoundException {
        Endpoint endpoint = serviceMetadata.getEndpoint(processIdentifier, transportProfiles);

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

    public Endpoint getEndpoint(Header header, TransportProfile... transportProfiles)
            throws LookupException, PeppolSecurityException, EndpointNotFoundException {
        return getEndpoint(header.getReceiver(), header.getDocumentType(),
                header.getProcess(), transportProfiles);
    }
}
