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

package network.oxalis.vefa.peppol.lookup;

import java.io.FileNotFoundException;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import network.oxalis.vefa.peppol.common.api.PotentiallySigned;
import network.oxalis.vefa.peppol.common.code.Service;
import network.oxalis.vefa.peppol.common.lang.EndpointNotFoundException;
import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.Endpoint;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessIdentifier;
import network.oxalis.vefa.peppol.common.model.Redirect;
import network.oxalis.vefa.peppol.common.model.ServiceInformation;
import network.oxalis.vefa.peppol.common.model.ServiceMetadata;
import network.oxalis.vefa.peppol.common.model.ServiceReference;
import network.oxalis.vefa.peppol.common.model.Signed;
import network.oxalis.vefa.peppol.common.model.TransportProfile;
import network.oxalis.vefa.peppol.lookup.api.FetcherResponse;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.MetadataFetcher;
import network.oxalis.vefa.peppol.lookup.api.MetadataLocator;
import network.oxalis.vefa.peppol.lookup.api.MetadataProvider;
import network.oxalis.vefa.peppol.lookup.api.MetadataReader;
import network.oxalis.vefa.peppol.mode.Mode;
import network.oxalis.vefa.peppol.security.api.CertificateValidator;
import network.oxalis.vefa.peppol.security.lang.PeppolSecurityException;

public class LookupClient {

    private final MetadataLocator locator;
    private final MetadataProvider provider;
    private final MetadataFetcher fetcher;
    private final MetadataReader reader;
    private final CertificateValidator validator;

    public LookupClient(Mode mode, Map<String, Object> objectStorage) {
    	LookupClientBuilder builder = (LookupClientBuilder) objectStorage.get(LookupClientBuilder.class.getName());
    	if (builder != null) {
            this.locator = builder.metadataLocator;
            this.provider = builder.metadataProvider;
            this.fetcher = builder.metadataFetcher;
            this.reader = builder.metadataReader;
            this.validator = builder.certificateValidator;
    	} else {
    		throw new IllegalArgumentException("Object storage is expected to contain a key "+LookupClientBuilder.class.getName()+" with a reference to a builder");
    	}
    }
    
    protected LookupClient(LookupClientBuilder builder) {
        this.locator = builder.metadataLocator;
        this.provider = builder.metadataProvider;
        this.fetcher = builder.metadataFetcher;
        this.reader = builder.metadataReader;
        this.validator = builder.certificateValidator;
    }

    public List<ServiceReference> getServiceReferences(ParticipantIdentifier participantIdentifier)
            throws LookupException {
        URI location = locator.lookup(participantIdentifier);
        List<URI>  serviceReferencesUriList = this.provider.resolveDocumentIdentifiers(location, participantIdentifier);

        FetcherResponse fetcherResponse;
        try {
            fetcherResponse = fetcher.fetch(serviceReferencesUriList);
        } catch (FileNotFoundException e) {
            throw new LookupException(String.format("Receiver (%s) not found.", participantIdentifier.toString()), e);
        }

        return reader.parseServiceGroup(fetcherResponse);
    }

    public List<DocumentTypeIdentifier> getDocumentIdentifiers(ParticipantIdentifier participantIdentifier)
            throws LookupException {
        return getServiceReferences(participantIdentifier).stream()
                .map(ServiceReference::getDocumentTypeIdentifier)
                .collect(Collectors.toList());
    }

    public ServiceMetadata getServiceMetadata(ParticipantIdentifier participantIdentifier,
                                              DocumentTypeIdentifier documentTypeIdentifier)
            throws LookupException, PeppolSecurityException {
        URI location = locator.lookup(participantIdentifier);
        List<URI> serviceMetaDataUriList = this.provider.resolveServiceMetadata(location, participantIdentifier, documentTypeIdentifier);

        PotentiallySigned<ServiceMetadata> potentiallySigned = getPotentiallySignedMetadata(participantIdentifier, documentTypeIdentifier, serviceMetaDataUriList);

        if (potentiallySigned instanceof Signed)
            validator.validate(Service.SMP, ((Signed<ServiceMetadata>) potentiallySigned).getCertificate());

        ServiceMetadata serviceMetadata = potentiallySigned.getContent();

        if (serviceMetadata.getServiceInformation() != null) {
            return serviceMetadata;
        }

        Redirect redirect = serviceMetadata.getRedirect();

        if (redirect != null) {
            return handleRedirect(participantIdentifier, documentTypeIdentifier, redirect);
        }

        throw new LookupException(String.format(
                "Combination of receiver (%s) and document type identifier (%s) is not supported.",
                participantIdentifier.toString(), documentTypeIdentifier.toString()));
    }

    private ServiceMetadata handleRedirect(ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier, Redirect redirect) throws LookupException, PeppolSecurityException {
        List<URI> redirectURIList = new ArrayList<URI>();
        redirectURIList.add(URI.create(redirect.getHref()));

        PotentiallySigned<ServiceMetadata> potentiallySigned = getPotentiallySignedMetadata(
                participantIdentifier, documentTypeIdentifier, redirectURIList);

        if (potentiallySigned instanceof Signed) {
            X509Certificate certificate = ((Signed<ServiceMetadata>) potentiallySigned).getCertificate();
            validator.validate(Service.SMP, certificate);
            if (!certificate.getSubjectX500Principal().getName().equals(redirect.getCertificateUID())) {
                throw new LookupException(String.format(
                        "Subject of redirected response for receiver (%s) and document type identifier (%s) does not match the expected value. Actual: '%s', Expected: '%s'",
                        participantIdentifier.toString(),
                        documentTypeIdentifier.toString(),
                        certificate.getSubjectX500Principal().getName(),
                        redirect.getCertificateUID()));
            }
        }

        ServiceMetadata serviceMetadata = potentiallySigned.getContent();

        if (serviceMetadata.getServiceInformation() != null) {
            return serviceMetadata;
        }

        throw new LookupException(String.format(
                "Redirect failed for receiver (%s) and document type identifier (%s) is not supported.",
                participantIdentifier.toString(), documentTypeIdentifier.toString()));
    }

    private PotentiallySigned<ServiceMetadata> getPotentiallySignedMetadata(ParticipantIdentifier participantIdentifier,
                                                                            DocumentTypeIdentifier documentTypeIdentifier,
                                                                            List<URI> serviceMetaDataUriList) throws LookupException, PeppolSecurityException {
        try {
            FetcherResponse fetcherResponse = fetcher.fetch(serviceMetaDataUriList);
            return reader.parseServiceMetadata(fetcherResponse);
        } catch (FileNotFoundException e) {
            throw new LookupException(String.format(
                    "Combination of receiver (%s) and document type identifier (%s) is not supported.",
                    participantIdentifier.toString(), documentTypeIdentifier.toString()), e);
        }
    }

    public Endpoint getEndpoint(ServiceMetadata serviceMetadata, ProcessIdentifier processIdentifier,
                                TransportProfile... transportProfiles)
            throws PeppolSecurityException, EndpointNotFoundException {
        ServiceInformation<Endpoint> serviceInformation = serviceMetadata.getServiceInformation();

        if (serviceInformation != null) {
            Endpoint endpoint = serviceInformation.getEndpoint(processIdentifier, transportProfiles);

            validator.validate(Service.AP, endpoint.getCertificate());

            return endpoint;
        }

        throw new EndpointNotFoundException(
                String.format("Combination of '%s' and transport profile(s) not found.", processIdentifier));
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
