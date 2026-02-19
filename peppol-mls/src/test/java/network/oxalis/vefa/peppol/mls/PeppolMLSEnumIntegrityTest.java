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

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PeppolMLSEnumIntegrityTest {

    @Test
    public void allMLSResponseCodesShouldMatchName() {
        for (MLSResponseCode code : MLSResponseCode.values()) {
            assertEquals(code.getCode(), code.name(),
                    "Mismatch in MLSResponseCode: " + code.name());
        }
    }

    @Test
    public void onlyREShouldBeRejection() {
        for (MLSResponseCode code : MLSResponseCode.values()) {
            if (code == MLSResponseCode.RE) {
                assertTrue(code.isRejection(), "RE must be rejection");
            } else {
                assertFalse(code.isRejection(),
                        code.name() + " must not be rejection");
            }
        }
    }

    @Test
    public void allMLSStatusReasonCodesShouldMatchName() {
        for (MLSStatusReasonCode code : MLSStatusReasonCode.values()) {
            assertEquals(code.getCode(), code.name(),
                    "Mismatch in MLSStatusReasonCode: " + code.name());
        }
    }

    @Test
    public void expectedMLSResponseCodeCountShouldRemainStable() {
        assertEquals(MLSResponseCode.values().length, 3,
                "Unexpected number of MLSResponseCode constants");
    }

    @Test
    public void expectedMLSStatusReasonCodeCountShouldRemainStable() {
        assertEquals(MLSStatusReasonCode.values().length, 4,
                "Unexpected number of MLSStatusReasonCode constants");
    }
}
