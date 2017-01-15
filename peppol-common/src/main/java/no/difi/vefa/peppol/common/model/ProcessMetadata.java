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

package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;

import java.io.Serializable;
import java.util.*;

public class ProcessMetadata implements Serializable {

    private static final long serialVersionUID = -8684282659539348955L;

    private ProcessIdentifier processIdentifier;

    private Map<TransportProfile, Endpoint> endpoints = new HashMap<>();

    public static ProcessMetadata of(ProcessIdentifier processIdentifier, Endpoint... endpoints) {
        return of(processIdentifier, Arrays.asList(endpoints));
    }

    public static ProcessMetadata of(ProcessIdentifier processIdentifier, List<Endpoint> endpoints) {
        return new ProcessMetadata(processIdentifier, endpoints);
    }

    private ProcessMetadata(ProcessIdentifier processIdentifier, List<Endpoint> endpoints) {
        this.processIdentifier = processIdentifier;

        for (Endpoint endpoint : endpoints)
            this.endpoints.put(endpoint.getTransportProfile(), endpoint);
    }

    public ProcessIdentifier getProcessIdentifier() {
        return processIdentifier;
    }

    public List<TransportProfile> getTransportProfiles() {
        return new ArrayList<>(endpoints.keySet());
    }

    public List<Endpoint> getEndpoints() {
        return new ArrayList<>(endpoints.values());
    }

    public Endpoint getEndpoint(TransportProfile... transportProfiles) throws EndpointNotFoundException {
        for (TransportProfile transportProfile : transportProfiles)
            if (endpoints.containsKey(transportProfile))
                return endpoints.get(transportProfile);

        throw new EndpointNotFoundException("Unable to find endpoint information for given transport profile(s).");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessMetadata that = (ProcessMetadata) o;

        if (!processIdentifier.equals(that.processIdentifier)) return false;
        return endpoints.equals(that.endpoints);

    }

    @Override
    public int hashCode() {
        int result = processIdentifier.hashCode();
        result = 31 * result + endpoints.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ProcessMetadata{" +
                "processIdentifier=" + processIdentifier +
                ", endpoints=" + endpoints +
                '}';
    }
}
