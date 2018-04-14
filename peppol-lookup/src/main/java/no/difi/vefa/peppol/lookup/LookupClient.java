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

package no.difi.vefa.peppol.lookup;

import no.difi.vefa.peppol.common.api.PotentiallySigned;
import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.lookup.api.*;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
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

    @Deprecated
    public List<DocumentTypeIdentifier> getDocumentIdentifiers(ParticipantIdentifier participantIdentifier)
            throws LookupException {
        List<DocumentTypeIdentifier> documentTypeIdentifiers = new ArrayList<>();

        for (ServiceReference serviceReference : getServiceReferences(participantIdentifier))
            documentTypeIdentifiers.add(serviceReference.getDocumentTypeIdentifier());

        return documentTypeIdentifiers;
    }

    public List<ServiceReference> getServiceReferences(ParticipantIdentifier participantIdentifier)
            throws LookupException {
        URI location = locator.lookup(participantIdentifier);
        URI provider = this.provider.resolveDocumentIdentifiers(location, participantIdentifier);

        FetcherResponse fetcherResponse;
        try {
            fetcherResponse = fetcher.fetch(provider);
        } catch (FileNotFoundException e) {
            throw new LookupException(String.format("Receiver (%s) not found.", participantIdentifier.toString()), e);
        }

        return reader.parseServiceGroup(fetcherResponse);
    }

    public ServiceMetadata getServiceMetadata(ParticipantIdentifier participantIdentifier,
                                              DocumentTypeIdentifier documentTypeIdentifier)
            throws LookupException, PeppolSecurityException {
        URI location = locator.lookup(participantIdentifier);
        URI provider = this.provider.resolveServiceMetadata(location, participantIdentifier, documentTypeIdentifier);

        FetcherResponse fetcherResponse;
        try {
            fetcherResponse = fetcher.fetch(provider);
        } catch (FileNotFoundException e) {
            throw new LookupException(String.format(
                    "Combination of receiver (%s) and document type identifier (%s) is not supported.",
                    participantIdentifier.toString(), documentTypeIdentifier.toString()), e);
        }

        PotentiallySigned<ServiceMetadata> serviceMetadata = reader.parseServiceMetadata(fetcherResponse);

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
