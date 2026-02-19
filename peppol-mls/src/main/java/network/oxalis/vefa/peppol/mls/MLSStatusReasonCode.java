/*
 * Copyright 2015-2026 Direktoratet for forvaltning og IKT
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

package network.oxalis.vefa.peppol.mls;

public enum MLSStatusReasonCode {

    BV("BV"), // Business rule violation fatal
    BW("BW"), // Business rule violation warning
    FD("FD"), // Failure of delivery
    SV("SV"); // Syntax violation

    private final String code;

    MLSStatusReasonCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
