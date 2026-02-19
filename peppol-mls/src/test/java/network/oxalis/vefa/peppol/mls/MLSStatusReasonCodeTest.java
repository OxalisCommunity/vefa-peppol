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

import static org.testng.Assert.assertEquals;

public class MLSStatusReasonCodeTest {

    @Test
    public void getCodeShouldReturnCorrectValues() {
        assertEquals(MLSStatusReasonCode.BV.getCode(), "BV");
        assertEquals(MLSStatusReasonCode.BW.getCode(), "BW");
        assertEquals(MLSStatusReasonCode.FD.getCode(), "FD");
        assertEquals(MLSStatusReasonCode.SV.getCode(), "SV");
    }

    @Test
    public void enumValuesShouldContainAllExpectedConstants() {
        MLSStatusReasonCode[] values = MLSStatusReasonCode.values();

        assertEquals(values.length, 4);
        assertEquals(values[0], MLSStatusReasonCode.BV);
        assertEquals(values[1], MLSStatusReasonCode.BW);
        assertEquals(values[2], MLSStatusReasonCode.FD);
        assertEquals(values[3], MLSStatusReasonCode.SV);
    }

    @Test
    public void valueOfShouldReturnCorrectEnumConstant() {
        assertEquals(MLSStatusReasonCode.valueOf("BV"), MLSStatusReasonCode.BV);
        assertEquals(MLSStatusReasonCode.valueOf("BW"), MLSStatusReasonCode.BW);
        assertEquals(MLSStatusReasonCode.valueOf("FD"), MLSStatusReasonCode.FD);
        assertEquals(MLSStatusReasonCode.valueOf("SV"), MLSStatusReasonCode.SV);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void valueOfShouldThrowExceptionForInvalidValue() {
        MLSStatusReasonCode.valueOf("INVALID");
    }

    @Test
    public void codeShouldMatchEnumName() {
        for (MLSStatusReasonCode code : MLSStatusReasonCode.values()) {
            assertEquals(code.getCode(), code.name(),
                    "Code mismatch for enum: " + code.name());
        }
    }
}

