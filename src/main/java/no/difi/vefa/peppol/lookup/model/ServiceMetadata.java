package no.difi.vefa.peppol.lookup.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceMetadata implements Serializable {

    private static final long serialVersionUID = -7523336374349545534L;

    private ParticipantIdentifier participantIdentifier;

    private DocumentIdentifier documentIdentifier;
    private ProcessIdentifier processIdentifier;

    private List<Endpoint> endpoints = new ArrayList<Endpoint>();
    private Map<String, Endpoint> endpointMap = new HashMap<String, Endpoint>();

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

    public ProcessIdentifier getProcessIdentifier() {
        return processIdentifier;
    }

    public void setProcessIdentifier(ProcessIdentifier processIdentifier) {
        this.processIdentifier = processIdentifier;
    }

    public void addEndpoint(Endpoint endpoint) {
        this.endpoints.add(endpoint);
        this.endpointMap.put(endpoint.getTransportProfile(), endpoint);
    }

    public List<Endpoint> getEndpoints() {
        return this.endpoints;
    }

    public Endpoint getEndpoint(String... transportProfiles) {
        for (String transportProfile : transportProfiles)
            if (endpointMap.containsKey(transportProfile))
                return endpointMap.get(transportProfile);

        return null;
    }
}
