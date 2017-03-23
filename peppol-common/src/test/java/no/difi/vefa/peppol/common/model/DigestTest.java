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

import no.difi.vefa.peppol.common.code.DigestMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DigestTest {

    @Test
    public void simple() {
        Digest d = Digest.of(DigestMethod.SHA256, "1".getBytes());

        Assert.assertEquals(d.getMethod(), DigestMethod.SHA256);
        Assert.assertEquals(d.getValue(), "1".getBytes());

        Assert.assertTrue(d.equals(d));
        Assert.assertFalse(d.equals(null));
        Assert.assertFalse(d.equals("1"));
        Assert.assertFalse(d.equals(Digest.of(DigestMethod.SHA1, "1".getBytes())));
        Assert.assertFalse(d.equals(Digest.of(DigestMethod.SHA256, "2".getBytes())));

        Assert.assertNotNull(d.hashCode());

        Assert.assertTrue(d.toString().contains("SHA256"));
    }

}
