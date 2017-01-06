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
