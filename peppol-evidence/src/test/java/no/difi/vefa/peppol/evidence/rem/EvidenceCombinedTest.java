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

import no.difi.vefa.peppol.common.code.DigestMethod;
import no.difi.vefa.peppol.common.model.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

public class EvidenceCombinedTest {

    @Test
    public void simple() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        EvidenceWriter.write(outputStream, EvidenceTest.EVIDENCE);

        Evidence newEvidence = EvidenceReader.read(new ByteArrayInputStream(outputStream.toByteArray()));

        Assert.assertEquals(newEvidence, EvidenceTest.EVIDENCE);
    }

    @Test
    public void noDocumentIdentifier() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        EvidenceWriter.write(outputStream, EvidenceTest.EVIDENCE.documentIdentifier(null));

        Evidence newEvidence = EvidenceReader.read(new ByteArrayInputStream(outputStream.toByteArray()));

        Assert.assertEquals(newEvidence, EvidenceTest.EVIDENCE.documentIdentifier(null));
    }

    @Test
    public void noPeppolExtension() throws Exception {
        Evidence evidence = Evidence.newInstance()
                .type(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT)
                .eventCode(EventCode.ACCEPTANCE)
                .eventReason(EventReason.OTHER)
                .issuer("Java Testing")
                .evidenceIdentifier(InstanceIdentifier.generateUUID())
                .timestamp(new Date())
                .sender(ParticipantIdentifier.of("9908:123456785"))
                .receiver(ParticipantIdentifier.of("9908:987654325"))
                .documentTypeIdentifier(DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1", Scheme.NONE))
                .messageIdentifier(InstanceIdentifier.generateUUID())
                .digest(Digest.of(DigestMethod.SHA256, "VGhpc0lzQVNIQTI1NkRpZ2VzdA==".getBytes()));

        Assert.assertFalse(evidence.hasPeppolExtensionValues());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        EvidenceWriter.write(outputStream, evidence);

        Evidence newEvidence = EvidenceReader.read(new ByteArrayInputStream(outputStream.toByteArray()));

        Assert.assertEquals(newEvidence, evidence);

        Assert.assertFalse(newEvidence.hasPeppolExtensionValues());
    }
}
