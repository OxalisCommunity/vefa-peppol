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

        assertEquals(new ParticipantIdentifier("9908:991825827").getIdentifier(), "9908:991825827");
        assertTrue(ParticipantIdentifier.of("9908:991825827").urlencoded().contains("991825827"));

        ParticipantIdentifier participantIdentifier = ParticipantIdentifier.of("9908:991825827");

        assertTrue(participantIdentifier.equals(participantIdentifier));
        assertFalse(participantIdentifier.equals("9908:991825827"));
        assertFalse(participantIdentifier.equals(null));

        assertFalse(participantIdentifier.equals(ParticipantIdentifier.of("9908:991825827", Scheme.of("Other"))));
    }
}
