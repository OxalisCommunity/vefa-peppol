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

import org.testng.Assert;
import org.testng.annotations.Test;

public class UnsignedTest {

    @Test
    public void simple() {
        Assert.assertEquals(Unsigned.of("1").getContent(), "1");

        Assert.assertTrue(Unsigned.of("1").equals(Unsigned.of("1")));
        Assert.assertFalse(Unsigned.of("1").equals(Unsigned.of("2")));
        Assert.assertFalse(Unsigned.of("1").equals("1"));
        Assert.assertFalse(Unsigned.of("1").equals(null));

        Unsigned<String> unsigned = Unsigned.of("1");
        Assert.assertTrue(unsigned.equals(unsigned));

        Unsigned<String> unsignedSubset = unsigned.ofSubset(unsigned.getContent());
        Assert.assertTrue(unsigned.equals(unsignedSubset));

        Assert.assertNotNull(Unsigned.of("1").hashCode());

        Assert.assertTrue(Unsigned.of("TEST").toString().contains("TEST"));
    }
}
