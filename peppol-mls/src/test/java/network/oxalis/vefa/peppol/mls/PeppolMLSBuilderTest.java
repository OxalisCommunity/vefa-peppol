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
import network.oxalis.peppol.ubl2.jaxb.cac.ResponseType;
import network.oxalis.peppol.ubl2.jaxb.cbc.EndpointIDType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class PeppolMLSBuilderTest extends AbstractPeppolTest {

    private PeppolMLSBuilder createBaseBuilder() {
        return new PeppolMLSBuilder(
                SENDER_ID,
                SCHEME_ID,
                RECEIVER_ID,
                SCHEME_ID
        );
    }

    @Test
    public void shouldBuildMinimalValidMLS() {

        String instanceId = UUID.randomUUID().toString();

        ApplicationResponseType mls = createBaseBuilder()
                .setDocumentResponse(instanceId, MLSResponseCode.RE.getCode(), "Rejected")
                .build();

        Assert.assertNotNull(mls);
        Assert.assertNotNull(mls.getID());
        Assert.assertNotNull(mls.getIssueDate());
        Assert.assertNotNull(mls.getIssueTime());

        Assert.assertEquals(mls.getDocumentResponse().size(), 1);

        DocumentResponseType documentResponse = mls.getDocumentResponse().get(0);

        Assert.assertEquals(
                documentResponse.getDocumentReference().get(0).getID().getValue(),
                instanceId
        );

        ResponseType response = documentResponse.getResponse();
        Assert.assertEquals(response.getResponseCode().getValue(), MLSResponseCode.RE.getCode());
        Assert.assertEquals(response.getDescription().get(0).getValue(), "Rejected");
    }

    @Test
    public void shouldSetCustomizationAndProfileFromEnum() {

        ApplicationResponseType mls = createBaseBuilder()
                .setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.AP.getCode(), null)
                .build();

        Assert.assertEquals(
                mls.getCustomizationID().getValue(),
                PeppolMLSProfile.MLS_1_0.getCustomizationId()
        );

        Assert.assertEquals(
                mls.getProfileID().getValue(),
                PeppolMLSProfile.MLS_1_0.getProfileId()
        );
    }

    @Test
    public void shouldGenerateUniqueUUIDForId() {

        ApplicationResponseType mls1 = createBaseBuilder()
                .setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.AB.getCode(), null)
                .build();

        ApplicationResponseType mls2 = createBaseBuilder()
                .setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.AB.getCode(), null)
                .build();

        Assert.assertNotEquals(
                mls1.getID().getValue(),
                mls2.getID().getValue()
        );
    }

    @Test
    public void shouldSetSenderAndReceiverEndpointCorrectly() {

        ApplicationResponseType mls = createBaseBuilder()
                .setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.AP.getCode(), null)
                .build();

        EndpointIDType sender = mls.getSenderParty().getEndpointID();
        EndpointIDType receiver = mls.getReceiverParty().getEndpointID();

        Assert.assertEquals(sender.getValue(), SENDER_ID);
        Assert.assertEquals(sender.getSchemeID(), SCHEME_ID);

        Assert.assertEquals(receiver.getValue(), RECEIVER_ID);
        Assert.assertEquals(receiver.getSchemeID(), SCHEME_ID);
    }

    @Test
    public void shouldAllowNullDescription() {

        ApplicationResponseType mls = createBaseBuilder()
                .setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.AP.getCode(), null)
                .build();

        ResponseType response = mls.getDocumentResponse().get(0).getResponse();

        Assert.assertEquals(response.getResponseCode().getValue(), MLSResponseCode.AP.getCode());
        Assert.assertTrue(response.getDescription().isEmpty());
    }

    @Test
    public void shouldAddLineResponse() {

        LineResponseType lineResponse =
                new PeppolMLSLineResponseBuilder("1")
                        .addResponse("Line rejected", MLSStatusReasonCode.BV)
                        .build();

        ApplicationResponseType mls = createBaseBuilder()
                .setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.RE.getCode(), "Rejected")
                .addLineResponse(lineResponse)
                .build();

        DocumentResponseType documentResponse = mls.getDocumentResponse().get(0);

        Assert.assertEquals(documentResponse.getLineResponse().size(), 1);

        LineResponseType result = documentResponse.getLineResponse().get(0);

        Assert.assertEquals(
                result.getLineReference().getLineID().getValue(),
                "1"
        );

        Assert.assertEquals(
                result.getResponse().get(0)
                        .getStatus().get(0)
                        .getStatusReasonCode()
                        .getValue(),
                MLSStatusReasonCode.BV.getCode()
        );
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldFailIfDocumentResponseMissing() {
        createBaseBuilder().build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailIfResponseCodeMissing() {
        createBaseBuilder()
                .setDocumentResponse(UUID.randomUUID().toString(), null, null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldFailIfSettingDocumentResponseTwice() {

        PeppolMLSBuilder builder = createBaseBuilder();

        builder.setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.AP.getCode(), null);
        builder.setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.AP.getCode(), null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldNotAllowMutationAfterBuild() {

        PeppolMLSBuilder builder = createBaseBuilder();

        builder.setDocumentResponse(UUID.randomUUID().toString(), MLSResponseCode.AP.getCode(), null);
        builder.build();

        builder.addLineResponse(
                new PeppolMLSLineResponseBuilder("1")
                        .addResponse("After build", MLSStatusReasonCode.BV)
                        .build()
        );
    }
}

