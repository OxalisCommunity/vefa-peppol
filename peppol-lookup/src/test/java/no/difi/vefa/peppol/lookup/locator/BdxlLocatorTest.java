/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

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
        Assert.assertEquals(
                locator.lookup(ParticipantIdentifier.of("9908:810418052")),
                URI.create("http://test-smp.difi.no.publisher.acc.edelivery.tech.ec.europa.eu")
        );
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
                        "B-eacf0eecc06f3fe1cff9e0e674201d99fc73affaf5aa6eccd3a30565.iso6523-actorid-upis." +
                                "acc.edelivery.tech.ec.europa.eu"
                ),
                "http://test-smp.difi.no.publisher.acc.edelivery.tech.ec.europa.eu"
        );
    }
}
