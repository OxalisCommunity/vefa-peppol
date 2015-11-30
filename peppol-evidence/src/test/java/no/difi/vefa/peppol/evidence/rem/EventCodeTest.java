package no.difi.vefa.peppol.evidence.rem;

import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

/**
 * Created by soc on 30.11.2015.
 */
public class EventCodeTest {

    @Test
    public void testValueFor() throws Exception {

        URI value = EventCode.DELIVERY_EXPIRATION.getValue();

        EventCode eventCode = EventCode.valueFor(value.toString());
        assertEquals(eventCode, EventCode.DELIVERY_EXPIRATION);
    }
}