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

public class SchemeTest {

    @Test
    public void simple() {
        Scheme schema = Scheme.of("SCHEME");
        Assert.assertEquals(schema.getIdentifier(), "SCHEME");
        Assert.assertEquals(schema.toString(), "SCHEME");
        Assert.assertNotNull(schema.hashCode());

        Assert.assertTrue(schema.equals(schema));
        Assert.assertFalse(schema.equals("SCHEME"));
        Assert.assertFalse(schema.equals(null));
    }
}
