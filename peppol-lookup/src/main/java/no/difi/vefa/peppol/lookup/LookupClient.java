/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
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

import no.difi.vefa.peppol.common.api.PotentiallySigned;
import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.lookup.api.*;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;

import java.net.URI;
import java.util.List;

public class LookupClient {

    private MetadataLocator locator;

    private MetadataProvider provider;

    private MetadataFetcher fetcher;

    private MetadataReader reader;

    private CertificateValidator validator;

    LookupClient(MetadataLocator locator, MetadataProvider provider, MetadataFetcher fetcher,
                 MetadataReader reader, CertificateValidator validator) {
        this.locator = locator;
        this.provider = provider;
        this.fetcher = fetcher;
        this.reader = reader;
        this.validator = validator;
    }

    public List<DocumentTypeIdentifier> getDocumentIdentifiers(ParticipantIdentifier participantIdentifier)
            throws LookupException {
        URI location = locator.lookup(participantIdentifier);
        URI provider = this.provider.resolveDocumentIdentifiers(location, participantIdentifier);

        return reader.parseDocumentIdentifiers(fetcher.fetch(provider));
    }

    public ServiceMetadata getServiceMetadata(ParticipantIdentifier participantIdentifier,
                                              DocumentTypeIdentifier documentTypeIdentifier)
            throws LookupException, PeppolSecurityException {
        URI location = locator.lookup(participantIdentifier);
        URI provider = this.provider.resolveServiceMetadata(location, participantIdentifier, documentTypeIdentifier);

        PotentiallySigned<ServiceMetadata> serviceMetadata = reader.parseServiceMetadata(fetcher.fetch(provider));

        if (serviceMetadata instanceof Signed)
            validator.validate(Service.SMP, ((Signed) serviceMetadata).getCertificate());

        return serviceMetadata.getContent();
    }

    public Endpoint getEndpoint(ServiceMetadata serviceMetadata, ProcessIdentifier processIdentifier,
                                TransportProfile... transportProfiles)
            throws PeppolSecurityException, EndpointNotFoundException {
        Endpoint endpoint = serviceMetadata.getEndpoint(processIdentifier, transportProfiles);

        validator.validate(Service.AP, endpoint.getCertificate());

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
