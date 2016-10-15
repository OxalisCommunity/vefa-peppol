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
        return new ServiceMetadata(participantIdentifier, documentTypeIdentifier, endpoints, signer);
    }

    @Deprecated
    public ServiceMetadata(ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier,
                           List<Endpoint> endpoints, X509Certificate signer) {
        this.participantIdentifier = participantIdentifier;
        this.documentTypeIdentifier = documentTypeIdentifier;
        this.endpoints = endpoints;
        this.signer = signer;

        for (Endpoint endpoint : endpoints)
            if (!this.processIdentifiers.contains(endpoint.getProcessIdentifier()))
                this.processIdentifiers.add(endpoint.getProcessIdentifier());
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
