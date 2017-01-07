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

import no.difi.vefa.peppol.common.model.Signed;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class SignedEvidenceCombinedTest {

    @Test
    public void simpleStream() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        SignedEvidenceWriter.write(outputStream, TestResources.getPrivateKey(), EvidenceTest.EVIDENCE);

        Signed<Evidence> evidenceSigned = SignedEvidenceReader.read(new ByteArrayInputStream(outputStream.toByteArray()));

        Assert.assertEquals(evidenceSigned.getCertificate(), TestResources.getPrivateKey().getCertificate());
        Assert.assertEquals(evidenceSigned.getContent(), EvidenceTest.EVIDENCE);
    }

    @Test
    public void simpleNode() throws Exception {
        Document document = SignedEvidenceWriter.write(TestResources.getPrivateKey(), EvidenceTest.EVIDENCE);

        Signed<Evidence> evidenceSigned = SignedEvidenceReader.read(document);

        Assert.assertEquals(evidenceSigned.getCertificate(), TestResources.getPrivateKey().getCertificate());
        Assert.assertEquals(evidenceSigned.getContent(), EvidenceTest.EVIDENCE);
    }
}
