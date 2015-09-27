package no.difi.vefa.edelivery.lookup.model;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class ServiceMetadata implements Serializable {

    private static final long serialVersionUID = -7523336374349545534L;

    private ParticipantIdentifier participantIdentifier;
    private DocumentIdentifier documentIdentifier;

    private List<ProcessIdentifier> processIdentifiers = new ArrayList<ProcessIdentifier>();
    private List<TransportProfile> transportProfiles = new ArrayList<TransportProfile>();

    private X509Certificate signer;

    private List<Endpoint> endpoints = new ArrayList<Endpoint>();

    public ParticipantIdentifier getParticipantIdentifier() {
        return participantIdentifier;
    }

    public void setParticipantIdentifier(ParticipantIdentifier participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public DocumentIdentifier getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(DocumentIdentifier documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public List<ProcessIdentifier> getProcessIdentifiers() {
        return processIdentifiers;
    }

    public List<TransportProfile> getTransportProfiles() {
        return transportProfiles;
    }

    public void addEndpoint(Endpoint endpoint) {
        if (!processIdentifiers.contains(endpoint.getProcessIdentifier()))
            processIdentifiers.add(endpoint.getProcessIdentifier());

        this.endpoints.add(endpoint);
    }

    public List<Endpoint> getEndpoints() {
        return this.endpoints;
    }

    public Endpoint getEndpoint(ProcessIdentifier processIdentifier, TransportProfile... transportProfiles) {
        for (TransportProfile transportProfile : transportProfiles)
            for (Endpoint endpoint : endpoints)
                if (endpoint.getTransportProfile().equals(transportProfile) && endpoint.getProcessIdentifier().equals(processIdentifier))
                    return endpoint;

        return null;
    }

    public X509Certificate getSigner() {
        return signer;
    }

    public void setSigner(X509Certificate signer) {
        this.signer = signer;
    }
}
