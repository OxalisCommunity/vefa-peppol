package network.oxalis.vefa.peppol.common.model;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MlsToIdentifierTest {

    @Test
    public void simple() {
        MlsToIdentifier mlsToIdentifier1 = MlsToIdentifier.of(
                "0242:000723", MlsToIdentifier.DEFAULT_SCHEME);
        MlsToIdentifier mlsToIdentifier2 = MlsToIdentifier.of(
                "0242:000724");

        Assert.assertTrue(mlsToIdentifier1.equals(mlsToIdentifier1));
        Assert.assertFalse(mlsToIdentifier1.equals(mlsToIdentifier1.getIdentifier()));
        Assert.assertFalse(mlsToIdentifier1.equals(null));

        Assert.assertFalse(mlsToIdentifier1.equals(mlsToIdentifier2));
        Assert.assertEquals(mlsToIdentifier1.getScheme(), mlsToIdentifier2.getScheme());
    }
}
