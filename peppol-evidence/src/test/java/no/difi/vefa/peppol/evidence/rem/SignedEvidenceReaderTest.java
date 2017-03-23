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
import org.testng.annotations.Test;
import org.w3c.dom.Node;

import java.io.InputStream;

public class SignedEvidenceReaderTest {

    @Test
    public void simpleConstructor() {
        new SignedEvidenceReader();
    }

    @Test(expectedExceptions = RemEvidenceException.class,
            expectedExceptionsMessageRegExp = "Node of type Document required\\.")
    public void exceptionOnNonDocumentType() throws Exception {
        SignedEvidenceReader.read(Mockito.mock(Node.class));
    }

    @Test(expectedExceptions = RemEvidenceException.class)
    public void exceptionOnInputStreamError() throws Exception {
        SignedEvidenceReader.read(Mockito.mock(InputStream.class));
    }

}
