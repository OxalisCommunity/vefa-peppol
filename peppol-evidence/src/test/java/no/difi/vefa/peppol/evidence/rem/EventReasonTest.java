package no.difi.vefa.peppol.evidence.rem;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by soc on 30.11.2015.
 */
public class EventReasonTest {

    @Test
    public void testValueForCode() throws Exception {

        String code = EventReason.INVALID_USER_SIGNATURE.getCode();
        EventReason eventReason = EventReason.valueForCode(code);
        assertEquals(EventReason.INVALID_USER_SIGNATURE, eventReason );
    }
}