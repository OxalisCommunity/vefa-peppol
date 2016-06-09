package no.difi.vefa.peppol.common.code;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ServiceTest {

    @Test
    public void simple() {
        for (Service service : Service.values())
            Assert.assertEquals(Service.valueOf(service.name()), service);
    }
}
