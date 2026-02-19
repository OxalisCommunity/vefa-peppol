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

public class MLSResponseCodeTest {

    @Test
    public void getCodeShouldReturnCorrectValues() {
        assertEquals(MLSResponseCode.AP.getCode(), "AP");
        assertEquals(MLSResponseCode.AB.getCode(), "AB");
        assertEquals(MLSResponseCode.RE.getCode(), "RE");
    }

    @Test
    public void isRejectionShouldReturnTrueOnlyForRE() {
        assertFalse(MLSResponseCode.AP.isRejection());
        assertFalse(MLSResponseCode.AB.isRejection());
        assertTrue(MLSResponseCode.RE.isRejection());
    }

    @Test
    public void enumValuesShouldContainAllExpectedConstants() {
        MLSResponseCode[] values = MLSResponseCode.values();

        assertEquals(values.length, 3);
        assertEquals(values[0], MLSResponseCode.AP);
        assertEquals(values[1], MLSResponseCode.AB);
        assertEquals(values[2], MLSResponseCode.RE);
    }

    @Test
    public void valueOfShouldReturnCorrectEnumConstant() {
        assertEquals(MLSResponseCode.valueOf("AP"), MLSResponseCode.AP);
        assertEquals(MLSResponseCode.valueOf("AB"), MLSResponseCode.AB);
        assertEquals(MLSResponseCode.valueOf("RE"), MLSResponseCode.RE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void valueOfShouldThrowExceptionForInvalidValue() {
        MLSResponseCode.valueOf("INVALID");
    }
}
