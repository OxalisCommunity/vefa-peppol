/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import no.difi.vefa.peppol.mode.Mode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

public class BdxlLocatorTest {

    @Test
    public void simple() throws Exception {
        MetadataLocator locator = new BdxlLocator(Mode.of("TEST"));
        Assert.assertEquals(
                locator.lookup(ParticipantIdentifier.of("9908:810418052")),
                URI.create("http://test-smp.difi.no/")
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
