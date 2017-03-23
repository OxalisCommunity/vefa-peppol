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

package no.difi.vefa.peppol.evidence.rem;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class EventReasonTest {

    @Test
    public void testValueForCode() throws Exception {
        String code = EventReason.INVALID_USER_SIGNATURE.getCode();
        EventReason eventReason = EventReason.valueForCode(code);
        assertEquals(EventReason.INVALID_USER_SIGNATURE, eventReason );
    }

    @Test
    public void testValueOf() {
        assertEquals(EventReason.OTHER, EventReason.valueOf("OTHER"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void valueForCodeException() {
        EventReason.valueForCode("Test...");
    }
}
