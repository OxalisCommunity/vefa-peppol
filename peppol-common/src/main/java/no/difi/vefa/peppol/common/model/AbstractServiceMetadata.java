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
import java.util.Collections;
import java.util.List;

public abstract class AbstractServiceMetadata<T extends SimpleEndpoint> implements Serializable {

    private static final long serialVersionUID = -7523336374349545534L;

    private final ParticipantIdentifier participantIdentifier;

    private final DocumentTypeIdentifier documentTypeIdentifier;

    private final List<ProcessMetadata<T>> processes;

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
            if (processMetadata.getProcessIdentifier().contains(processIdentifier))
                return processMetadata.getEndpoint(transportProfiles);

        throw new EndpointNotFoundException(
                String.format("Combination of '%s' and transport profile(s) not found.", processIdentifier));
    }
}
