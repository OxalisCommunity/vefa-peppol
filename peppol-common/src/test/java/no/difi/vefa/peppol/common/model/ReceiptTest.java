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

public class ReceiptTest {

    @Test
    public void simple() {
        Receipt r1 = Receipt.of("Value".getBytes());
        Receipt r2 = Receipt.of("text/plain", "Value".getBytes());

        Assert.assertEquals(r1.getValue(), "Value".getBytes());
        Assert.assertNull(r1.getType());
        Assert.assertEquals(r2.getType(), "text/plain");

        Assert.assertTrue(r1.equals(r1));
        Assert.assertFalse(r1.equals(null));
        Assert.assertFalse(r1.equals("Test"));
        Assert.assertTrue(r1.equals(Receipt.of("Value".getBytes())));
        Assert.assertTrue(r2.equals(Receipt.of("text/plain", "Value".getBytes())));
        Assert.assertFalse(r1.equals(r2));
        Assert.assertFalse(r2.equals(r1));

        Assert.assertNotNull(r1.hashCode());
        Assert.assertNotNull(r2.hashCode());

        Assert.assertTrue(r2.toString().contains("text/plain"));
    }
}
