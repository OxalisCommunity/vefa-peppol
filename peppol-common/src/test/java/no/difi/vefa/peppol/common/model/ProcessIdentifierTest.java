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

import no.difi.vefa.peppol.common.lang.PeppolParsingException;
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

    @Test
    public void simpleParse() throws Exception {
        ProcessIdentifier processIdentifier = ProcessIdentifier
                .parse("qualifier::identifier");

        Assert.assertEquals(processIdentifier.getIdentifier(), "identifier");
        Assert.assertEquals(processIdentifier.getScheme().getIdentifier(), "qualifier");

        try {
            ProcessIdentifier.parse("value");
            Assert.fail();
        } catch (PeppolParsingException e) {
            // Valid!
        }
    }
}
