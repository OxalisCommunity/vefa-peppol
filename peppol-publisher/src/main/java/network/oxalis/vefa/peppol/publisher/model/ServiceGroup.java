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

package network.oxalis.vefa.peppol.publisher.model;

import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ServiceReference;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * @author erlend
 */
public class ServiceGroup implements Serializable {

    private static final long serialVersionUID = -4268277692087478476L;

    private final ParticipantIdentifier participantIdentifier;

    private final Set<ServiceReference> serviceReferences;

    public static ServiceGroup of(ParticipantIdentifier participantIdentifier,
                                  Set<ServiceReference> serviceReferences) {
        return new ServiceGroup(participantIdentifier, serviceReferences);
    }

    private ServiceGroup(ParticipantIdentifier participantIdentifier,
                         Set<ServiceReference> serviceReferences) {
        this.participantIdentifier = participantIdentifier;
        this.serviceReferences = serviceReferences;
    }

    public ParticipantIdentifier getParticipantIdentifier() {
        return participantIdentifier;
    }

    public Set<ServiceReference> getServiceReferences() {
        return Collections.unmodifiableSet(serviceReferences);
    }
}
