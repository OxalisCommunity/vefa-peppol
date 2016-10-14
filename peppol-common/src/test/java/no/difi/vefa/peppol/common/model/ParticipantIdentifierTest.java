package no.difi.vefa.peppol.common.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ParticipantIdentifierTest {

    @Test
    public void simple() {
        assertEquals(ParticipantIdentifier.of("9908:991825827").toString(), "iso6523-actorid-upis::9908:991825827");
        assertEquals(ParticipantIdentifier.of("9908:difi").toString(), "iso6523-actorid-upis::9908:difi");
        assertEquals(ParticipantIdentifier.of(" 9908:DIFI ").toString(), "iso6523-actorid-upis::9908:difi");

        assertEquals(ParticipantIdentifier.of("9908:991825827").toString(), "iso6523-actorid-upis::9908:991825827");
        assertEquals(ParticipantIdentifier.of("9908:991825827").getScheme(), Scheme.of("iso6523-actorid-upis"));

        assertEquals(ParticipantIdentifier.of("else", Scheme.of("something")).toString(), "something::else");
    }
}
