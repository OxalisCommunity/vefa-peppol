package no.difi.vefa.peppol.common.model;

import org.testng.Assert;
import org.testng.annotations.Test;

public class InstanceIdentifierTest {

    @Test
    public void simple() {
        Assert.assertNotNull(InstanceIdentifier.generateUUID().getValue());

        InstanceIdentifier identifier = InstanceIdentifier.of("TEST");

        Assert.assertEquals(identifier.getValue(), "TEST");
        Assert.assertEquals(identifier.toString(), "TEST");
    }

}
