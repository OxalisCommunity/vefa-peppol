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
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertNotNull;

public class PeppolMLSTest extends AbstractPeppolTest {

    @Test
    public void validMLSShouldPassValidation() {

        PeppolMLSLineResponseBuilder line = lineResponse("1", "Business rule violation fatal", MLSStatusReasonCode.BV);

        ApplicationResponseType mls =
                defaultBuilder()
                        .setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.RE.getCode(), "Rejected")
                        .addLineResponse(line.build())
                        .build();

        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test
    public void missingDocumentReferenceShouldFail() {

        ApplicationResponseType mls = new ApplicationResponseType();

        Assert.expectThrows(
                IllegalStateException.class,
                () -> PeppolMLSIntegrityValidator.validate(mls)
        );
    }

    @Test
    public void testMarshalMLSShouldProduceValidXml() {

        PeppolMLSLineResponseBuilder line = lineResponse("1", "Business rule violation fatal", MLSStatusReasonCode.BV);

        ApplicationResponseType mls =
                defaultBuilder()
                        .setDocumentResponse(UUID.randomUUID().toString(),
                                MLSResponseCode.RE.getCode(),
                                "Rejected")
                        .addLineResponse(line.build())
                        .build();

        String xml = PeppolMLSMarshaller.marshalToString(mls);

        assertNotNull(xml);

        assertTrue(xml.matches("(?s).*<.*ResponseCode>RE</.*ResponseCode>.*"));
        assertTrue(xml.matches("(?s).*<.*StatusReasonCode>BV</.*StatusReasonCode>.*"));

        System.out.println(">>>>> MARSHALLED XML <<<<<");
        System.out.println(xml);
    }


    @Test
    public void testUnmarshalExistingMLSXml() throws Exception {

        String xml = loadXml("/mls/good/MessageLevelStatus_Example_RE.xml");

        ApplicationResponseType mls = PeppolMLSMarshaller.unmarshal(xml);

        assertNotNull(mls);

        assertEquals(
                mls.getDocumentResponse().get(0)
                        .getResponse()
                        .getResponseCode().getValue(),
                "RE");

        assertFalse(mls.getDocumentResponse().get(0)
                .getLineResponse().isEmpty());

        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test
    public void testRoundTripMarshalUnmarshal() {

        PeppolMLSLineResponseBuilder line = lineResponse("1", "Business rule violation fatal", MLSStatusReasonCode.BV);

        ApplicationResponseType original =
                defaultBuilder()
                        .setDocumentResponse(UUID.randomUUID().toString(),
                                MLSResponseCode.RE.getCode(),
                                "Rejected")
                        .addLineResponse(line.build())
                        .build();

        String xml = PeppolMLSMarshaller.marshalToString(original);
        System.out.println(">>>>> Round Trip MARSHALLED XML <<<<<");
        System.out.println(xml);

        ApplicationResponseType roundTripped = PeppolMLSMarshaller.unmarshal(xml);
        System.out.println("<<<<< Round Trip UNMARSHALLED XML >>>>>");
        System.out.println(PeppolMLSMarshaller.marshalToString(roundTripped));

        assertNotNull(roundTripped);
        assertEquals(
                MLSResponseCode.RE.getCode(),
                roundTripped.getDocumentResponse().get(0)
                        .getResponse()
                        .getResponseCode().getValue());

        assertEquals(
                MLSStatusReasonCode.BV.getCode(),
                roundTripped.getDocumentResponse().get(0)
                        .getLineResponse().get(0)
                        .getResponse().get(0)
                        .getStatus().get(0)
                        .getStatusReasonCode().getValue());

        PeppolMLSIntegrityValidator.validate(roundTripped);
    }

    @Test
    public void testABResponseShouldPassValidation() throws Exception {

        String mlsXML = loadXml("/mls/good/MessageLevelStatus_Example_AB.xml");

        ApplicationResponseType mls = PeppolMLSMarshaller.unmarshal(mlsXML);

        assertNotNull(mls);
        assertEquals(
                mls.getDocumentResponse().get(0)
                        .getResponse()
                        .getResponseCode().getValue(),
                "AB");

        assertDoesNotThrow(() ->
                PeppolMLSIntegrityValidator.validate(mls));
    }

    @Test
    public void testAPResponseShouldPassValidation() throws Exception {

        String mlsXML = loadXml("/mls/good/MessageLevelStatus_Example_AP.xml");

        ApplicationResponseType mls = PeppolMLSMarshaller.unmarshal(mlsXML);

        assertNotNull(mls);
        assertEquals(
                mls.getDocumentResponse().get(0)
                        .getResponse()
                        .getResponseCode().getValue(),
                "AP");


        assertDoesNotThrow(() ->
                PeppolMLSIntegrityValidator.validate(mls));
    }

    @Test
    public void testREResponseShouldPassValidationWithLineResponse() throws Exception {

        String mlsXML = loadXml("/mls/good/MessageLevelStatus_Example_RE.xml");

        ApplicationResponseType mls = PeppolMLSMarshaller.unmarshal(mlsXML);

        assertNotNull(mls);
        assertEquals(
                mls.getDocumentResponse().get(0)
                        .getResponse()
                        .getResponseCode().getValue(),
                "RE");

        assertFalse(mls.getDocumentResponse().get(0).getLineResponse().isEmpty());

        assertDoesNotThrow(() ->
                PeppolMLSIntegrityValidator.validate(mls));
    }

}
