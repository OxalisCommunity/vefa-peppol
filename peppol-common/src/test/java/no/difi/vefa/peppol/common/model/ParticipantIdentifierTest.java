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

import static org.testng.Assert.*;

public class ParticipantIdentifierTest {

    @Test
    public void simple() {
        assertEquals(ParticipantIdentifier.of("9908:991825827").toString(), "iso6523-actorid-upis::9908:991825827");
        assertEquals(ParticipantIdentifier.of("9908:difi").toString(), "iso6523-actorid-upis::9908:difi");
        assertEquals(ParticipantIdentifier.of(" 9908:DIFI ").toString(), "iso6523-actorid-upis::9908:difi");

        assertEquals(ParticipantIdentifier.of("9908:991825827").toString(), "iso6523-actorid-upis::9908:991825827");
        assertEquals(ParticipantIdentifier.of("9908:991825827").getScheme(), Scheme.of("iso6523-actorid-upis"));

        assertEquals(ParticipantIdentifier.of("else", Scheme.of("something")).toString(), "something::else");

        assertEquals(ParticipantIdentifier.of("9908:991825827").getIdentifier(), "9908:991825827");
        assertTrue(ParticipantIdentifier.of("9908:991825827").urlencoded().contains("991825827"));

        ParticipantIdentifier participantIdentifier = ParticipantIdentifier.of("9908:991825827");

        assertTrue(participantIdentifier.equals(participantIdentifier));
        assertFalse(participantIdentifier.equals("9908:991825827"));
        assertFalse(participantIdentifier.equals(null));

        assertFalse(participantIdentifier.equals(ParticipantIdentifier.of("9908:991825827", Scheme.of("Other"))));
    }

    @Test
    public void simpleParse() throws Exception {
        ParticipantIdentifier participantIdentifier = ParticipantIdentifier
                .parse("qualifier::identifier");

        Assert.assertEquals(participantIdentifier.getIdentifier(), "identifier");
        Assert.assertEquals(participantIdentifier.getScheme().getIdentifier(), "qualifier");

        try {
            ParticipantIdentifier.parse("value");
            Assert.fail();
        } catch (PeppolParsingException e) {
            // Valid!
        }
    }

}
