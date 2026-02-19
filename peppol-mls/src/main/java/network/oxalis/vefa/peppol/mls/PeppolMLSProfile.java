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

public enum PeppolMLSProfile {

    MLS_1_0("urn:peppol:edec:mls:1.0", "urn:peppol:edec:mls");

    private final String customizationId;
    private final String profileId;

    PeppolMLSProfile(String customizationId, String profileId) {
        this.customizationId = customizationId;
        this.profileId = profileId;
    }

    public String getCustomizationId() {
        return customizationId;
    }

    public String getProfileId() {
        return profileId;
    }
}
