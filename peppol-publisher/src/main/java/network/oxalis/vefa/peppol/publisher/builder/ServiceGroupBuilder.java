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

import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ServiceReference;
import network.oxalis.vefa.peppol.publisher.model.ServiceGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * @author erlend
 */
public class ServiceGroupBuilder {

    private ParticipantIdentifier participantIdentifier;
    private final Set<ServiceReference> serviceReferences = new HashSet<>();

    public static ServiceGroupBuilder newInstance(ParticipantIdentifier participantIdentifier) {
        return new ServiceGroupBuilder(participantIdentifier);
    }

    private ServiceGroupBuilder(ParticipantIdentifier participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public ServiceGroupBuilder add(ServiceReference serviceReference) {
        this.serviceReferences.add(serviceReference);
        return this;
    }

    public ServiceGroup build() {
        return ServiceGroup.of(participantIdentifier, serviceReferences);
    }
}
