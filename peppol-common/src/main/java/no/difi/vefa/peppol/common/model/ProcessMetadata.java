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

package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.api.SimpleEndpoint;
import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;

import java.io.Serializable;
import java.util.*;

public class ProcessMetadata<T extends SimpleEndpoint> implements Serializable {

    private static final long serialVersionUID = -8684282659539348955L;

    private final List<ProcessIdentifier> processIdentifier;

    private final Map<TransportProfile, T> endpoints = new HashMap<>();

    public static <T extends SimpleEndpoint> ProcessMetadata<T> of(
            ProcessIdentifier processIdentifier, T... endpoints) {
        return of(Collections.singletonList(processIdentifier), Arrays.asList(endpoints));
    }

    public static <T extends SimpleEndpoint> ProcessMetadata<T> of(
            List<ProcessIdentifier> processIdentifier, T... endpoints) {
        return of(processIdentifier, Arrays.asList(endpoints));
    }

    public static <T extends SimpleEndpoint> ProcessMetadata<T> of(
            ProcessIdentifier processIdentifier, List<T> endpoints) {
        return new ProcessMetadata<>(Collections.singletonList(processIdentifier), endpoints);
    }

    public static <T extends SimpleEndpoint> ProcessMetadata<T> of(
            List<ProcessIdentifier> processIdentifier, List<T> endpoints) {
        return new ProcessMetadata<>(processIdentifier, endpoints);
    }

    private ProcessMetadata(List<ProcessIdentifier> processIdentifiers, List<T> endpoints) {
        this.processIdentifier = processIdentifiers;

        for (T endpoint : endpoints)
            this.endpoints.put(endpoint.getTransportProfile(), endpoint);
    }

    public List<ProcessIdentifier> getProcessIdentifier() {
        return processIdentifier;
    }

    public List<TransportProfile> getTransportProfiles() {
        return new ArrayList<>(endpoints.keySet());
    }

    public List<T> getEndpoints() {
        return new ArrayList<>(endpoints.values());
    }

    public T getEndpoint(TransportProfile... transportProfiles) throws EndpointNotFoundException {
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
