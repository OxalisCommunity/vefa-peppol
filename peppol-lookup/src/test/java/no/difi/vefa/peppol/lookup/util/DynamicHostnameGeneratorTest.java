/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
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

package no.difi.vefa.peppol.lookup.util;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.LookupException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DynamicHostnameGeneratorTest {

    @Test
    public void simpleMd5() throws LookupException {
        DynamicHostnameGenerator generator =
                new DynamicHostnameGenerator("B-", "edelivery.tech.ec.europa.eu", "MD5");

        assertEquals(generator.generate(ParticipantIdentifier.of("9908:difi")),
                "B-42fabff13df16391dbd1f01b7c05d0e7.iso6523-actorid-upis." +
                        "edelivery.tech.ec.europa.eu");
        assertEquals(generator.generate(ParticipantIdentifier.of("9908:DIFI")),
                "B-42fabff13df16391dbd1f01b7c05d0e7.iso6523-actorid-upis." +
                        "edelivery.tech.ec.europa.eu");
    }

    @Test
    public void simpleSHA224() throws LookupException {
        DynamicHostnameGenerator generator =
                new DynamicHostnameGenerator("B-", "acc.edelivery.tech.ec.europa.eu", "SHA-224");

        assertEquals(generator.generate(ParticipantIdentifier.of("0088:5798000000001")),
                "B-fc932ca4494194a43ebb039cefe51a6c1d8c771afd2039bfb7f76e7f.iso6523-actorid-upis." +
                        "acc.edelivery.tech.ec.europa.eu");
    }

    @Test(expectedExceptions = LookupException.class)
    public void triggerException() throws Exception {
        new DynamicHostnameGenerator("B-", "acc.edelivery.tech.ec.europa.eu", "SHA-224").generate(null);
    }
}
