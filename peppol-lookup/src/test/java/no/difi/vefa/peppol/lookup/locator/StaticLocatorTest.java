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

package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StaticLocatorTest {

    @Test
    public void simple() throws Exception {
        String uri = "http://smp.example.com/";

        MetadataLocator locator = new StaticLocator(uri);

        Assert.assertEquals(locator.lookup(ParticipantIdentifier.of("9908:991825827")).toString(), uri);
    }
}
