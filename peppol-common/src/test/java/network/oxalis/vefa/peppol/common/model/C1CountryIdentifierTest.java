package network.oxalis.vefa.peppol.common.model;

import org.testng.Assert;
import org.testng.annotations.Test;

public class C1CountryIdentifierTest {

    @Test
    public void simple() {
        C1CountryIdentifier c1CountryIdentifier = C1CountryIdentifier.of("IN");

        Assert.assertEquals(c1CountryIdentifier.getIdentifier(), "IN");
        Assert.assertEquals(c1CountryIdentifier.toString(), "IN");

        Assert.assertTrue(c1CountryIdentifier.equals(c1CountryIdentifier));
        Assert.assertFalse(c1CountryIdentifier.equals("IN"));
        Assert.assertFalse(c1CountryIdentifier.equals(null));
    }

}
