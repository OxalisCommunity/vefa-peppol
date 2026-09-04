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
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RoundTripMLSIntTest extends AbstractPeppolTest {

    @Test
    public void buildSerializeParseAndVerify() {

        String originalSbdhInstanceIdentifier = UUID.randomUUID().toString();

        // Create the Java MLS object.
        PeppolMLSLineResponseBuilder line =
                lineResponse("1", "Business rule violation fatal", MLSStatusReasonCode.BV);

        ApplicationResponseType created = defaultBuilder()
                .setDocumentResponse(originalSbdhInstanceIdentifier, MLSResponseCode.RE.getCode(), "Rejected")
                .addLineResponse(line.build())
                .build();

        // Serialize to XML.
        String xml = PeppolMLSMarshaller.marshalToString(created);
        System.out.println(">>>>> Serialized MLS XML <<<<<");
        System.out.println(xml);

        // Parse the XML back into a fresh Java object - simulating what the receiving party of this MLS response would do.
        ApplicationResponseType parsedBack = PeppolMLSMarshaller.unmarshal(xml);
        assertNotNull(parsedBack);

        // Structural validation must still pass on the round-tripped object.
        PeppolMLSIntegrityValidator.validate(parsedBack);

        // Verify every value survived the round trip.
        assertEquals(parsedBack.getSenderParty().getEndpointID().getValue(), SENDER_ID);
        assertEquals(parsedBack.getSenderParty().getEndpointID().getSchemeID(), SCHEME_ID);
        assertEquals(parsedBack.getReceiverParty().getEndpointID().getValue(), RECEIVER_ID);

        assertEquals(
                parsedBack.getDocumentResponse().get(0).getDocumentReference().get(0).getID().getValue(),
                originalSbdhInstanceIdentifier);

        assertEquals(
                parsedBack.getDocumentResponse().get(0).getResponse().getResponseCode().getValue(),
                MLSResponseCode.RE.getCode());

        assertEquals(
                parsedBack.getDocumentResponse().get(0).getResponse().getDescription().get(0).getValue(),
                "Rejected");

        assertEquals(
                parsedBack.getDocumentResponse().get(0)
                        .getLineResponse().get(0)
                        .getLineReference().getLineID().getValue(),
                "1");

        assertEquals(
                parsedBack.getDocumentResponse().get(0)
                        .getLineResponse().get(0)
                        .getResponse().get(0)
                        .getStatus().get(0)
                        .getStatusReasonCode().getValue(),
                MLSStatusReasonCode.BV.getCode());
    }
}
