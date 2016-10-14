package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class ServiceMetadata implements Serializable {

    private static final long serialVersionUID = -7523336374349545534L;

    private ParticipantIdentifier participantIdentifier;

    private DocumentTypeIdentifier documentTypeIdentifier;

    private List<ProcessIdentifier> processIdentifiers = new ArrayList<>();

    private List<TransportProfile> transportProfiles = new ArrayList<>();

    private X509Certificate signer;

    private List<Endpoint> endpoints;

    public static ServiceMetadata of(ParticipantIdentifier participantIdentifier,
                                     DocumentTypeIdentifier documentTypeIdentifier, List<Endpoint> endpoints,
                                     X509Certificate signer) {

        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.participantIdentifier = participantIdentifier;
        serviceMetadata.documentTypeIdentifier = documentTypeIdentifier;
        serviceMetadata.endpoints = endpoints;
        serviceMetadata.signer = signer;

        for (Endpoint endpoint : endpoints)
            if (!serviceMetadata.processIdentifiers.contains(endpoint.getProcessIdentifier()))
                serviceMetadata.processIdentifiers.add(endpoint.getProcessIdentifier());

        return serviceMetadata;
    }

    @Deprecated
    public ServiceMetadata() {
    }

    public ParticipantIdentifier getParticipantIdentifier() {
        return participantIdentifier;
    }

    public DocumentTypeIdentifier getDocumentTypeIdentifier() {
        return documentTypeIdentifier;
    }

    public List<ProcessIdentifier> getProcessIdentifiers() {
        return processIdentifiers;
    }

    public List<TransportProfile> getTransportProfiles() {
        return transportProfiles;
    }

    public List<Endpoint> getEndpoints() {
        return this.endpoints;
    }

    public Endpoint getEndpoint(ProcessIdentifier processIdentifier, TransportProfile... transportProfiles)
            throws EndpointNotFoundException {
        for (TransportProfile transportProfile : transportProfiles)
            for (Endpoint endpoint : endpoints)
                if (endpoint.getTransportProfile().equals(transportProfile)
                        && endpoint.getProcessIdentifier().equals(processIdentifier))
                    return endpoint;

        throw new EndpointNotFoundException(
                String.format("Combination of '%s' and transport profile(s) not found.", processIdentifier));
    }

    public X509Certificate getSigner() {
        return signer;
    }
}
