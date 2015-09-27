package no.difi.vefa.edelivery.lookup.locator;

import no.difi.vefa.edelivery.lookup.api.LookupException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class BusdoxLocatorTest {

    private BusdoxLocator busdoxLocator = new BusdoxLocator();

    @Test
    public void simple() throws LookupException{
        assertEquals(busdoxLocator.lookup("9908:991825827").getHost(), "b-770c6f5843e9e302de47ae4026307076.iso6523-actorid-upis.edelivery.tech.ec.europa.eu");

        assertEquals(busdoxLocator.lookup("9908:difi").getHost(), "b-42fabff13df16391dbd1f01b7c05d0e7.iso6523-actorid-upis.edelivery.tech.ec.europa.eu");
        assertEquals(busdoxLocator.lookup("9908:DIFI").getHost(), "b-42fabff13df16391dbd1f01b7c05d0e7.iso6523-actorid-upis.edelivery.tech.ec.europa.eu");
    }

}
