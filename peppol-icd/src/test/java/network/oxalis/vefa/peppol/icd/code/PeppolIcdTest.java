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

package network.oxalis.vefa.peppol.icd.code;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PeppolIcdTest {

    @Test
    public void simple() {
        Assert.assertEquals(PeppolIcd.BE_EN.getIdentifier(), "BE:EN");
        Assert.assertEquals(PeppolIcd.BE_EN.getCode(), "0208");

        Assert.assertNotNull(PeppolIcd.valueOf("BE_EN"));
        Assert.assertNotNull(PeppolIcd.valueOf("BE_EN").getScheme());

        Assert.assertEquals(PeppolIcd.findByCode("0208"), PeppolIcd.BE_EN);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exceptionOnUnknownCode() {
        PeppolIcd.findByCode("invalid");
    }
}
