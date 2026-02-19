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

import network.oxalis.peppol.ubl2.jaxb.cac.LineReferenceType;
import network.oxalis.peppol.ubl2.jaxb.cac.LineResponseType;
import network.oxalis.peppol.ubl2.jaxb.cac.ResponseType;
import network.oxalis.peppol.ubl2.jaxb.cac.StatusType;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PeppolMLSLineResponseBuilderTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void constructorShouldFailWhenLineIdIsNull() {
        new PeppolMLSLineResponseBuilder(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void constructorShouldFailWhenLineIdIsBlank() {
        new PeppolMLSLineResponseBuilder("  ");
    }


    @Test
    public void constructorShouldSetLineReferenceCorrectly() {

        PeppolMLSLineResponseBuilder builder =
                new PeppolMLSLineResponseBuilder("10");

        LineResponseType result = builder
                .addResponse("OK", MLSStatusReasonCode.BV)
                .build();

        assertNotNull(result);

        LineReferenceType lineReference = result.getLineReference();
        assertNotNull(lineReference);
        assertNotNull(lineReference.getLineID());
        assertEquals(lineReference.getLineID().getValue(), "10");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void addLineResponseShouldFailIfDescriptionIsNull() {

        new PeppolMLSLineResponseBuilder("1")
                .addResponse(null, MLSStatusReasonCode.BV);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void addLineResponseShouldFailIfDescriptionIsBlank() {

        new PeppolMLSLineResponseBuilder("1")
                .addResponse("   ", MLSStatusReasonCode.BV);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void addResponseShouldFailIfStatusReasonCodeIsNull() {

        new PeppolMLSLineResponseBuilder("1")
                .addResponse("Description", null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void buildShouldFailIfNoResponsesAdded() {

        new PeppolMLSLineResponseBuilder("1").build();
    }

    @Test
    public void shouldBuildSingleResponseCorrectly() {

        LineResponseType result =
                new PeppolMLSLineResponseBuilder("25")
                        .addResponse(
                                "Business rule violation",
                                MLSStatusReasonCode.BV
                        )
                        .build();

        assertNotNull(result);
        assertEquals(result.getResponse().size(), 1);

        ResponseType response = result.getResponse().get(0);

        assertEquals(response.getDescription().size(), 1);
        assertEquals(
                response.getDescription().get(0).getValue(),
                "Business rule violation"
        );

        assertEquals(response.getStatus().size(), 1);
        StatusType status = response.getStatus().get(0);
        assertEquals(
                status.getStatusReasonCode().getValue(),
                MLSStatusReasonCode.BV.getCode()
        );
    }

    @Test
    public void shouldBuildMultipleResponsesCorrectly() {

        LineResponseType result =
                new PeppolMLSLineResponseBuilder("50")
                        .addResponse("First issue", MLSStatusReasonCode.BV)
                        .addResponse("Second issue", MLSStatusReasonCode.SV)
                        .build();

        assertNotNull(result);
        assertEquals(result.getResponse().size(), 2);

        ResponseType first = result.getResponse().get(0);
        ResponseType second = result.getResponse().get(1);

        assertEquals(
                first.getDescription().get(0).getValue(),
                "First issue"
        );
        assertEquals(
                first.getStatus().get(0).getStatusReasonCode().getValue(),
                MLSStatusReasonCode.BV.getCode()
        );

        assertEquals(
                second.getDescription().get(0).getValue(),
                "Second issue"
        );
        assertEquals(
                second.getStatus().get(0).getStatusReasonCode().getValue(),
                MLSStatusReasonCode.SV.getCode()
        );
    }

    @Test
    public void builderShouldAllowChainedBuildCalls() {

        PeppolMLSLineResponseBuilder builder =
                new PeppolMLSLineResponseBuilder("10")
                        .addResponse("First", MLSStatusReasonCode.BV);

        LineResponseType first = builder.build();
        LineResponseType second = builder.build();

        assertSame(first, second);
    }
}
