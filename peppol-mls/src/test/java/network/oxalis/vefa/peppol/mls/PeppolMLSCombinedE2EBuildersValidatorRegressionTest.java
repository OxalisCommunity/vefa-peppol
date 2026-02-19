/*
 * Copyright 2015-2026 Direktoratet for forvaltning og IKT
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

package network.oxalis.vefa.peppol.mls;

import network.oxalis.peppol.ubl2.jaxb.ApplicationResponseType;
import network.oxalis.peppol.ubl2.jaxb.cac.DocumentResponseType;
import network.oxalis.peppol.ubl2.jaxb.cac.LineResponseType;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class PeppolMLSCombinedE2EBuildersValidatorRegressionTest extends AbstractPeppolTest {

    @Test
    public void goldenFileShouldMatchGeneratedXml() throws Exception {
        ApplicationResponseType mls = buildValidMLS("90f14eff-3705-4869-ad3c-caae270a234e");
        String generated = PeppolMLSMarshaller.marshalToString(mls);
        String xml = loadXml("/mls/good/MessageLevelStatus_Example_RE.xml");

        Diff diff = DiffBuilder.compare(xml)
                .withTest(generated)
                .ignoreWhitespace()
                .ignoreComments()
                .checkForSimilar()
                .build();

        Assert.assertFalse(diff.hasDifferences(),
                "Generated XML differs from golden file: " + diff.toString());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void validatorShouldFailWhenStatusReasonCodeMissing() {
        ApplicationResponseType mls = buildValidMLS(UUID.randomUUID().toString());
        mls.getDocumentResponse().get(0)
                .getLineResponse().get(0)
                .getResponse().get(0)
                .getStatus().get(0)
                .setStatusReasonCode(null);

        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test(
            expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp =
                    "Exactly one DocumentResponse is required"
    )
    public void validatorShouldFailWhenMultipleDocumentResponses() {
        ApplicationResponseType mls = buildMinimalValidMLS(UUID.randomUUID().toString());
        mls.getDocumentResponse().add(new DocumentResponseType());
        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test
    public void fullBuilderPipelineShouldProduceValidMLS() {

        LineResponseType line =
                new PeppolMLSLineResponseBuilder("1")
                        .addResponse("Business rule violation warning", MLSStatusReasonCode.BW)
                        .build();

        ApplicationResponseType mls =
                new PeppolMLSBuilder("12345678", "0242", "87654321", "0242")
                        .setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.RE.getCode(), "Rejected")
                        .addLineResponse(line)
                        .build();

        PeppolMLSIntegrityValidator.validate(mls);

        Assert.assertEquals(
                mls.getDocumentResponse().get(0)
                        .getLineResponse().size(),
                1
        );
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void builderShouldNotAllowReuse() {
        PeppolMLSBuilder builder =
                new PeppolMLSBuilder("12345678", "0242", "87654321", "0242")
                        .setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.AP.getCode(), null);

        builder.build();
        builder.build();
    }

    @Test
    public void xmlSampleShouldUnmarshalAndValidate() throws Exception {

        String mlsXML = loadXml("/mls/good/MessageLevelStatus_Example_AB.xml");

        ApplicationResponseType mls = PeppolMLSMarshaller.unmarshal(mlsXML);

        PeppolMLSIntegrityValidator.validate(mls);
    }


    private ApplicationResponseType buildMinimalValidMLS(String originalSbdhInstanceIdentifier) {
        return new PeppolMLSBuilder("123456", "0299", "234567", "0299")
                .setDocumentResponse(originalSbdhInstanceIdentifier, MLSResponseCode.RE.getCode(), "Rejected due to validation errors")
                .build();
    }

    private ApplicationResponseType buildValidMLS(String originalSbdhInstanceIdentifier) {
        LineResponseType line =
                new PeppolMLSLineResponseBuilder("/Catalogue/cac:CatalogueLine[3]/cac:Item[1]/cac:ClassifiedTaxCategory[1]/cbc:ID[1]")
                        .addResponse("Validation gives error [CL-T77-R002]- Tax categories MUST be coded using UN/ECE 5305 code list",
                                MLSStatusReasonCode.BV)
                        .build();

        return new PeppolMLSBuilder("123456", "0299", "234567", "0299")
                .withIdMLS(() -> "MLS-ID123")
                .withIssueDateMLS(() -> LocalDate.of(2025, 3, 11))
                .withIssueTimeMLS(() -> OffsetTime.of(LocalTime.of(12, 00, 00), ZoneOffset.UTC))
                .setDocumentResponse(originalSbdhInstanceIdentifier, MLSResponseCode.RE.getCode(), "Rejected due to validation errors")
                .addLineResponse(line)
                .build();
    }
}
