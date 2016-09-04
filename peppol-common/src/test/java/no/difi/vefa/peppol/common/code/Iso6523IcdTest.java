package no.difi.vefa.peppol.common.code;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Iso6523IcdTest {

    @Test
    public void simple() {
        Assert.assertEquals(Iso6523Icd.valueOfIcd("9908"), Iso6523Icd.NO_ORGNR);
        Assert.assertEquals(Iso6523Icd.valueOfIdentifier("NO:ORGNR"), Iso6523Icd.NO_ORGNR);

        Assert.assertEquals(Iso6523Icd.NO_ORGNR.getIdentifier(), "NO:ORGNR");
        Assert.assertEquals(Iso6523Icd.NO_ORGNR.getCode(), "9908");
        Assert.assertFalse(Iso6523Icd.NO_ORGNR.isDeprecated());

        Assert.assertNotNull(Iso6523Icd.valueOf("NO_ORGNR"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void throwExceptionOnInvalidIcd() {
        Iso6523Icd.valueOfIcd("Invalid");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void throwExceptionOnInvalidIdentifier() {
        Iso6523Icd.valueOfIdentifier("Invalid");
    }
}
