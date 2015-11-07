package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

public class BdxlLocatorTest {

    @Test
    public void simple() throws Exception {
        MetadataLocator locator = new BdxlLocator(DynamicLocator.OPENPEPPOL_TEST);
        Assert.assertEquals(locator.lookup(new ParticipantIdentifier("9908:810418052")), URI.create("http://test-smp.difi.no.publisher.acc.edelivery.tech.ec.europa.eu"));
    }

    @Test
    public void testRegexHandler() {
        // BDXL Specification (modified)
        Assert.assertEquals(
                BdxlLocator.handleRegex(
                        // "!^B-(+[0-9a-fA-F]).sid.peppol.eu$!https://serviceprovider.peppol.eu/\\1!",
                        "!^B-([0-9a-fA-F]+).sid.peppol.eu$!https://serviceprovider.peppol.eu/\\\\1!",
                        "B-eacf0eecc06f3fe1cff9e0e674201d99fc73affaf5aa6eccd3a30565.sid.peppol.eu"
                ),
                "https://serviceprovider.peppol.eu/eacf0eecc06f3fe1cff9e0e674201d99fc73affaf5aa6eccd3a30565"
        );

        // Proper
        Assert.assertEquals(
                BdxlLocator.handleRegex(
                        "!^.*$!http://test-smp.difi.no.publisher.acc.edelivery.tech.ec.europa.eu!",
                        "B-eacf0eecc06f3fe1cff9e0e674201d99fc73affaf5aa6eccd3a30565.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu"
                ),
                "http://test-smp.difi.no.publisher.acc.edelivery.tech.ec.europa.eu"
        );
    }

}
