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

package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;

public class EvidenceReaderTest {

    @Test
    public void simpleConstructor() {
        new EvidenceReader();
    }

    @Test(expectedExceptions = RemEvidenceException.class)
    public void exceptionOnInputStreamError() throws Exception {
        EvidenceReader.read(Mockito.mock(InputStream.class));
    }

    @Test(expectedExceptions = RemEvidenceException.class, expectedExceptionsMessageRegExp = "Version .*")
    public void exceptionOnInvalidVersion() throws Exception {
        EvidenceReader.read(getClass().getResourceAsStream("/test-version-1.xml"));
    }

    @Test
    public void readSimpleFile() throws Exception {
        Evidence evidence = EvidenceReader.read(getClass().getResourceAsStream("/sample-signed-formatted-rem.xml"));
        Assert.assertFalse(evidence.hasPeppolExtensionValues());
    }
}
