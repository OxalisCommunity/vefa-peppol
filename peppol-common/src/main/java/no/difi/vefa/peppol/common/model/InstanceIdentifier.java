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
import java.util.UUID;

public class InstanceIdentifier extends AbstractSimpleIdentifier implements Serializable {

    private static final long serialVersionUID = 3616828001672136897L;

    public static InstanceIdentifier generateUUID() {
        return of(UUID.randomUUID().toString());
    }

    public static InstanceIdentifier of(String value) {
        return new InstanceIdentifier(value);
    }

    public InstanceIdentifier(String value) {
        super(value);
    }

}
