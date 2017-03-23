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

package no.difi.vefa.peppol.lookup.provider;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.MetadataProvider;

import java.net.URI;

public class DefaultProvider implements MetadataProvider {

    @Override
    public URI resolveDocumentIdentifiers(URI location, ParticipantIdentifier participant) {
        return location.resolve(String.format("/%s",
                        participant.urlencoded())
        );
    }

    @Override
    public URI resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier,
                                      DocumentTypeIdentifier documentTypeIdentifier) {
        return location.resolve(String.format("/%s/services/%s",
                        participantIdentifier.urlencoded(),
                        documentTypeIdentifier.urlencoded())
        );
    }
}
