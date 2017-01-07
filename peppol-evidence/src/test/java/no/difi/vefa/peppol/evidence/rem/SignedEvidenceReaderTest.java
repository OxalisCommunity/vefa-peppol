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

    @Test(expectedExceptions = RemEvidenceException.class, expectedExceptionsMessageRegExp = "Node of type Document required\\.")
    public void exceptionOnNonDocumentType() throws Exception {
        SignedEvidenceReader.read(Mockito.mock(Node.class));
    }

    @Test(expectedExceptions = RemEvidenceException.class)
    public void exceptionOnInputStreamError() throws Exception {
        SignedEvidenceReader.read(Mockito.mock(InputStream.class));
    }

}
