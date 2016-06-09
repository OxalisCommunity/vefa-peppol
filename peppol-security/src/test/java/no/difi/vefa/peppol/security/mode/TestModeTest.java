package no.difi.vefa.peppol.security.mode;

import no.difi.vefa.peppol.common.code.Service;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestModeTest {

    @Test
    public void simple() {
        Assert.assertEquals(new TestMode().getIssuersInternal(Service.ALL).length, 0);
    }
}
