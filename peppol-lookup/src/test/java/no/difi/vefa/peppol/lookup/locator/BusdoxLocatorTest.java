package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.lookup.api.LookupException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class BusdoxLocatorTest {

    private BusdoxLocator busdoxLocator = new BusdoxLocator(DynamicLocator.OPENPEPPOL_PRODUCTION);

    @Test
    public void simple() throws LookupException{
        assertEquals(busdoxLocator.lookup("9908:991825827").getHost(), "B-770c6f5843e9e302de47ae4026307076.iso6523-actorid-upis.edelivery.tech.ec.europa.eu");
    }

}
