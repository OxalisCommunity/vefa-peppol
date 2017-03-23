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
                .documentTypeIdentifier(DocumentTypeIdentifier.of(
                        "urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender" +
                                "##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1", Scheme.NONE))
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
