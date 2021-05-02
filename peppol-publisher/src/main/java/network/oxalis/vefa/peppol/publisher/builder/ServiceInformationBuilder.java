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

package network.oxalis.vefa.peppol.publisher.builder;

import network.oxalis.vefa.peppol.common.model.*;
import network.oxalis.vefa.peppol.publisher.model.PublisherEndpoint;

import java.util.ArrayList;
import java.util.List;

public class ServiceInformationBuilder {

    private ParticipantIdentifier participantIdentifier;

    private DocumentTypeIdentifier documentTypeIdentifier;

    private final List<ProcessMetadata<PublisherEndpoint>> processes = new ArrayList<>();

    public static ServiceInformationBuilder newInstance() {
        return new ServiceInformationBuilder();
    }

    private ServiceInformationBuilder() {

    }

    public ServiceInformationBuilder participant(ParticipantIdentifier participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
        return this;
    }

    public ServiceInformationBuilder documentTypeIdentifier(DocumentTypeIdentifier documentTypeIdentifier) {
        this.documentTypeIdentifier = documentTypeIdentifier;
        return this;
    }

    public ServiceInformationBuilder add(ProcessIdentifier processIdentifier, PublisherEndpoint... endpoints) {
        this.processes.add(ProcessMetadata.of(processIdentifier, endpoints));
        return this;
    }

    public ServiceInformationBuilder add(ProcessIdentifier processIdentifier, List<PublisherEndpoint> endpoints) {
        this.processes.add(ProcessMetadata.of(processIdentifier, endpoints));
        return this;
    }

    public ServiceInformation<PublisherEndpoint> build() {
        return ServiceInformation.of(participantIdentifier, documentTypeIdentifier, processes);
    }
}
