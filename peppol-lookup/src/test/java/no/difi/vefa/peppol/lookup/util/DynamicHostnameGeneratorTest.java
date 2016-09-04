package no.difi.vefa.peppol.lookup.util;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.locator.DynamicLocator;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DynamicHostnameGeneratorTest {

    @Test
    public void simpleMd5() throws LookupException {
        DynamicHostnameGenerator generator = new DynamicHostnameGenerator("B-", DynamicLocator.OPENPEPPOL_PRODUCTION, "MD5");

        assertEquals(generator.generate(new ParticipantIdentifier("9908:difi")),
                "B-42fabff13df16391dbd1f01b7c05d0e7.iso6523-actorid-upis.edelivery.tech.ec.europa.eu");
        assertEquals(generator.generate(new ParticipantIdentifier("9908:DIFI")),
                "B-42fabff13df16391dbd1f01b7c05d0e7.iso6523-actorid-upis.edelivery.tech.ec.europa.eu");
    }

    @Test
    public void simpleSHA224() throws LookupException {
        DynamicHostnameGenerator generator = new DynamicHostnameGenerator("B-", DynamicLocator.OPENPEPPOL_TEST, "SHA-224");

        assertEquals(generator.generate(new ParticipantIdentifier("0088:5798000000001")),
                "B-fc932ca4494194a43ebb039cefe51a6c1d8c771afd2039bfb7f76e7f.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu");
    }

    @Test(expectedExceptions = LookupException.class)
    public void triggerException() throws Exception {
        new DynamicHostnameGenerator("B-", DynamicLocator.OPENPEPPOL_TEST, "SHA-224").generate(null);
    }

}
