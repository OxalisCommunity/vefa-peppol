/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
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

    public InstanceIdentifier() {
        super(null);
    }
}
