package no.difi.vefa.edelivery.lookup.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ParticipantIdentifierTest {

    @Test
    public void simple() {
        assertEquals(new ParticipantIdentifier("9908:991825827").getIdentifier(), "9908:991825827");
        assertEquals(new ParticipantIdentifier("9908:difi").getIdentifier(), "9908:difi");
        assertEquals(new ParticipantIdentifier(" 9908:DIFI ").getIdentifier(), "9908:difi");
    }

}
