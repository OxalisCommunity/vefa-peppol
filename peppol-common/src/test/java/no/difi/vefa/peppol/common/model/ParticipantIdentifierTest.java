package no.difi.vefa.peppol.common.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ParticipantIdentifierTest {

    @Test
    public void simple() {
        assertEquals(new ParticipantIdentifier("9908:991825827").toString(), "9908:991825827");
        assertEquals(new ParticipantIdentifier("9908:difi").toString(), "9908:difi");
        assertEquals(new ParticipantIdentifier(" 9908:DIFI ").toString(), "9908:difi");

        assertEquals(new ParticipantIdentifier("9908:991825827").toString(), "9908:991825827");
        assertEquals(new ParticipantIdentifier("9908:991825827").getScheme(), new Scheme("iso6523-actorid-upis"));
    }

}
