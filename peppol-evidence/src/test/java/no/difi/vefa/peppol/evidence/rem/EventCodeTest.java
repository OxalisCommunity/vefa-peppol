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

package no.difi.vefa.peppol.evidence.rem;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class EventCodeTest {

    @Test
    public void testValueFor() throws Exception {
        String value = EventCode.DELIVERY_EXPIRATION.getValue();

        EventCode eventCode = EventCode.valueFor(value);
        assertEquals(eventCode, EventCode.DELIVERY_EXPIRATION);
    }

    @Test
    public void testValueOf() {
        assertEquals(EventCode.valueOf("ACCEPTANCE"), EventCode.ACCEPTANCE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void valueForException() {
        EventCode.valueFor("Test...");
    }
}
