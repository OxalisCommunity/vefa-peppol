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

package no.difi.vefa.peppol.publisher.model;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author erlend
 */
public class ServiceGroup implements Serializable {

    private static final long serialVersionUID = -4268277692087478476L;

    private ParticipantIdentifier participantIdentifier;

    private List<DocumentTypeIdentifier> documentTypeIdentifiers;

    public static ServiceGroup of(ParticipantIdentifier participantIdentifier,
                                  List<DocumentTypeIdentifier> documentTypeIdentifiers) {
        return new ServiceGroup(participantIdentifier, documentTypeIdentifiers);
    }

    private ServiceGroup(ParticipantIdentifier participantIdentifier,
                        List<DocumentTypeIdentifier> documentTypeIdentifiers) {
        this.participantIdentifier = participantIdentifier;
        this.documentTypeIdentifiers = documentTypeIdentifiers;
    }

    public ParticipantIdentifier getParticipantIdentifier() {
        return participantIdentifier;
    }

    public List<DocumentTypeIdentifier> getDocumentTypeIdentifiers() {
        return Collections.unmodifiableList(documentTypeIdentifiers);
    }
}
