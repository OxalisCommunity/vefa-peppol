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

import org.testng.Assert;
import org.testng.annotations.Test;

public class PeppolMLSProfileTest {

    @Test
    public void testEnumHasExactlyOneConstant() {
        PeppolMLSProfile[] values = PeppolMLSProfile.values();

        Assert.assertEquals(values.length, 1,
                "PeppolMlsProfile enum should contain exactly one constant");

        Assert.assertEquals(values[0], PeppolMLSProfile.MLS_1_0,
                "The only allowed profile must be MLS_1_0");
    }

    @Test
    public void testCustomizationIdValueIsCorrect() {
        PeppolMLSProfile profile = PeppolMLSProfile.MLS_1_0;

        Assert.assertEquals(
                profile.getCustomizationId(),
                "urn:peppol:edec:mls:1.0",
                "CustomizationID must match the frozen MLS 1.0 value"
        );
    }

    @Test
    public void testProfileIdValueIsCorrect() {
        PeppolMLSProfile profile = PeppolMLSProfile.MLS_1_0;

        Assert.assertEquals(
                profile.getProfileId(),
                "urn:peppol:edec:mls",
                "ProfileID must match the frozen MLS profile value"
        );
    }

    @Test
    public void testEnumNameIsStable() {
        Assert.assertEquals(
                PeppolMLSProfile.MLS_1_0.name(),
                "MLS_1_0",
                "Enum name must not change (freeze contract)"
        );
    }

    @Test
    public void testValueOfReturnsCorrectConstant() {
        PeppolMLSProfile profile =
                PeppolMLSProfile.valueOf("MLS_1_0");

        Assert.assertEquals(
                profile,
                PeppolMLSProfile.MLS_1_0,
                "valueOf should resolve MLS_1_0 correctly"
        );
    }
}

