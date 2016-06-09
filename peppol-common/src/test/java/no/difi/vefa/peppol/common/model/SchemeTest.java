package no.difi.vefa.peppol.common.model;


import org.testng.Assert;
import org.testng.annotations.Test;

public class SchemeTest {

    @Test
    public void simple() {
        Scheme schema = new Scheme("SCHEME");
        Assert.assertEquals(schema.getValue(), "SCHEME");
        Assert.assertEquals(schema.toString(), "SCHEME");
        Assert.assertNotNull(schema.hashCode());

        Assert.assertFalse(schema.equals(null));
        Assert.assertTrue(schema.equals(schema));
    }

}
