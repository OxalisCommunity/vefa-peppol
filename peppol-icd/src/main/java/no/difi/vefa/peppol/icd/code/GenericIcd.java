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

package no.difi.vefa.peppol.icd.code;

import no.difi.vefa.peppol.common.model.Scheme;
import no.difi.vefa.peppol.icd.api.Icd;

/**
 * @author erlend
 */
public class GenericIcd implements Icd {

    private final String identifier;

    private final String code;

    private final Scheme scheme;

    private final String issuingAgency;

    public static Icd of(String identifier, String code, Scheme scheme) {
        return new GenericIcd(identifier, code, scheme, null);
    }

    public static Icd of(String identifier, String code, Scheme scheme, String issuingAgency) {
        return new GenericIcd(identifier, code, scheme, issuingAgency);
    }

    private GenericIcd(String identifier, String code, Scheme scheme, String issuingAgency) {
        this.identifier = identifier;
        this.code = code;
        this.scheme = scheme;
        this.issuingAgency = issuingAgency;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Scheme getScheme() {
        return scheme;
    }

    @Override
    public String getIssuingAgency() {
        return null;
    }
}
