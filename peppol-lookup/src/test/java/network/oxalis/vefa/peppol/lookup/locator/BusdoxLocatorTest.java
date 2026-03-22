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

package network.oxalis.vefa.peppol.lookup.locator;

import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.NotFoundException;
import network.oxalis.vefa.peppol.mode.Mode;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class BusdoxLocatorTest {

    private final BusdoxLocator busdoxLocator = new BusdoxLocator(Mode.of("PRODUCTION"));

    @Test
    public void simple() throws LookupException {
        try {
            busdoxLocator.lookup("0192:991825827");
            fail("Expected Exception - CNAME not supported");
        } catch (NotFoundException e) {
            //Expected
        }
    }
}
