package network.oxalis.vefa.peppol.common.model;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MlsTypeIdentifierTest {

    @Test
    public void simple() {
        MlsTypeIdentifier mlsTypeIdentifier = MlsTypeIdentifier.of("ALWAYS_SEND");

        Assert.assertEquals(mlsTypeIdentifier.getIdentifier(), "ALWAYS_SEND");
        Assert.assertEquals(mlsTypeIdentifier.toString(), "ALWAYS_SEND");

        Assert.assertTrue(mlsTypeIdentifier.equals(mlsTypeIdentifier));
        Assert.assertFalse(mlsTypeIdentifier.equals("ALWAYS_SEND"));
        Assert.assertFalse(mlsTypeIdentifier.equals(null));
    }
}
