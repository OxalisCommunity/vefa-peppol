package no.difi.vefa.peppol.icd.code;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Iso6523IcdTest {

    @Test
    public void simple() {
        Assert.assertEquals(Iso6523Icd.NO_ORGNR.getIdentifier(), "NO:ORGNR");
        Assert.assertEquals(Iso6523Icd.NO_ORGNR.getCode(), "9908");

        Assert.assertNotNull(Iso6523Icd.valueOf("NO_ORGNR"));
    }
}
