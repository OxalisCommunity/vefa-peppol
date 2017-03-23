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

package no.difi.vefa.peppol.icd.model;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.icd.api.Icd;

import java.io.Serializable;

/**
 * @author erlend
 */
public class IcdIdentifier implements Serializable {

    private static final long serialVersionUID = -7908081727801249085L;

    private final Icd icd;

    private final String identifier;

    public static IcdIdentifier of(Icd icd, String identifier) {
        return new IcdIdentifier(icd, identifier);
    }

    private IcdIdentifier(Icd icd, String identifier) {
        this.icd = icd;
        this.identifier = identifier;
    }

    public Icd getIcd() {
        return icd;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ParticipantIdentifier toParticipantIdentifier() {
        return ParticipantIdentifier.of(String.format("%s:%s", icd.getCode(), identifier), icd.getScheme());
    }

    public String toString() {
        return String.format("%s::%s:%s", icd.getScheme(), icd.getCode(), identifier);
    }
}

