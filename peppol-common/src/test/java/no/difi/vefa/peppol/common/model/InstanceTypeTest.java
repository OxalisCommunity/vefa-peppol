/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
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
