/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
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
