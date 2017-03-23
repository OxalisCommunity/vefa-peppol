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

import java.io.Serializable;

public class TransportProfile extends AbstractSimpleIdentifier implements Serializable {

    private static final long serialVersionUID = -8215053834194901976L;

    public static final TransportProfile START = TransportProfile.of("busdox-transport-start");

    public static final TransportProfile AS2_1_0 = TransportProfile.of("busdox-transport-as2-ver1p0");

    public static final TransportProfile AS4 = TransportProfile.of("bdxr-transport-ebms3-as4-v1p0");

    public static TransportProfile of(String value) {
        return new TransportProfile(value);
    }

    private TransportProfile(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "TransportProfile{" + value + '}';
    }
}
