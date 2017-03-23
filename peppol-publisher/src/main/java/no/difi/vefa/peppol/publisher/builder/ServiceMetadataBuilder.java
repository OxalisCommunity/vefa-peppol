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

package no.difi.vefa.peppol.publisher.builder;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.ProcessMetadata;
import no.difi.vefa.peppol.publisher.model.PublisherEndpoint;
import no.difi.vefa.peppol.publisher.model.PublisherServiceMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author erlend
 */
public class ServiceMetadataBuilder {

    private ParticipantIdentifier participantIdentifier;

    private DocumentTypeIdentifier documentTypeIdentifier;

    private List<ProcessMetadata<PublisherEndpoint>> processes = new ArrayList<>();

    public static ServiceMetadataBuilder newInstance() {
        return new ServiceMetadataBuilder();
    }

    private ServiceMetadataBuilder() {

    }

    public ServiceMetadataBuilder participant(ParticipantIdentifier participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
        return this;
    }

    public ServiceMetadataBuilder documentTypeIdentifier(DocumentTypeIdentifier documentTypeIdentifier) {
        this.documentTypeIdentifier = documentTypeIdentifier;
        return this;
    }

    public ServiceMetadataBuilder add(ProcessIdentifier processIdentifier, PublisherEndpoint... endpoints) {
        this.processes.add(ProcessMetadata.of(processIdentifier, endpoints));
        return this;
    }

    public PublisherServiceMetadata build() {
        return new PublisherServiceMetadata(participantIdentifier, documentTypeIdentifier, processes);
    }
}
