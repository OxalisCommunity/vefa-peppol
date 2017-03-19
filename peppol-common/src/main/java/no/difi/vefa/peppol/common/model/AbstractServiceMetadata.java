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

import no.difi.vefa.peppol.common.SimpleEndpoint;
import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public abstract class AbstractServiceMetadata<T extends SimpleEndpoint> implements Serializable {

    private static final long serialVersionUID = -7523336374349545534L;

    private ParticipantIdentifier participantIdentifier;

    private DocumentTypeIdentifier documentTypeIdentifier;

    private List<ProcessMetadata<T>> processes;

    protected AbstractServiceMetadata(ParticipantIdentifier participantIdentifier,
                                      DocumentTypeIdentifier documentTypeIdentifier,
                                      List<ProcessMetadata<T>> processes) {
        this.participantIdentifier = participantIdentifier;
        this.documentTypeIdentifier = documentTypeIdentifier;
        this.processes = processes;
    }

    public ParticipantIdentifier getParticipantIdentifier() {
        return participantIdentifier;
    }

    public DocumentTypeIdentifier getDocumentTypeIdentifier() {
        return documentTypeIdentifier;
    }

    public List<ProcessMetadata<T>> getProcesses() {
        return Collections.unmodifiableList(processes);
    }

    public T getEndpoint(ProcessIdentifier processIdentifier, TransportProfile... transportProfiles)
            throws EndpointNotFoundException {
        for (ProcessMetadata<T> processMetadata : processes)
            if (processMetadata.getProcessIdentifier().equals(processIdentifier))
                return processMetadata.getEndpoint(transportProfiles);

        throw new EndpointNotFoundException(
                String.format("Combination of '%s' and transport profile(s) not found.", processIdentifier));
    }
}
