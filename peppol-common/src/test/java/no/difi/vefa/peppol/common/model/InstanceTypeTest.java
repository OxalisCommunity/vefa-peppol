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

public class InstanceTypeTest {

    @Test
    public void simple() {
        InstanceType instanceType = InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "2.0");

        Assert.assertEquals(instanceType.getStandard(), "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
        Assert.assertEquals(instanceType.getType(), "Invoice");
        Assert.assertEquals(instanceType.getVersion(), "2.0");

        Assert.assertTrue(instanceType.equals(instanceType));
        Assert.assertFalse(instanceType.equals("Invoice"));
        Assert.assertFalse(instanceType.equals(null));

        Assert.assertFalse(instanceType.equals(
                InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-3", "Invoice", "2.0")));
        Assert.assertFalse(instanceType.equals(
                InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "CreditNote", "2.0")));
        Assert.assertFalse(instanceType.equals(
                InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "3.0")));
    }
}
