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
