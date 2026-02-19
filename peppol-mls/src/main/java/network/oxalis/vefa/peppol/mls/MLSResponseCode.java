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

public enum MLSResponseCode {

    AP("AP"),   // Delivered with confirmation
    AB("AB"),   // Delivered without confirmation
    RE("RE");   // Rejected

    private final String code;

    MLSResponseCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean isRejection() {
        return this == RE;
    }
}
