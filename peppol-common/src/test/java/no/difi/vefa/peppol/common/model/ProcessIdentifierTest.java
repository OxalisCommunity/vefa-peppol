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

public class ProcessIdentifierTest {

    @Test
    public void simple() {
        ProcessIdentifier process04 = ProcessIdentifier.of(
                "urn:www.cenbii.eu:profile:bii04:ver1.0", ProcessIdentifier.DEFAULT_SCHEME);
        ProcessIdentifier process05 = ProcessIdentifier.of(
                "urn:www.cenbii.eu:profile:bii05:ver1.0");

        Assert.assertTrue(process04.equals(process04));
        Assert.assertFalse(process04.equals(process04.getIdentifier()));
        Assert.assertFalse(process04.equals(null));

        Assert.assertFalse(process04.equals(process05));
        Assert.assertEquals(process04.getScheme(), process05.getScheme());
    }
}
