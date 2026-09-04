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

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class WriteMLSIntTest {

    private static final String SENDER_ID = "000723";
    private static final String RECEIVER_ID = "000010";
    private static final String SCHEME_ID = "0242";

    @Test
    public void createAndSerializeRejectionResponse() {

        // Fields supplied by the validation engine application: what happened to which line(s) of the original document, and why.
        PeppolMLSLineResponseBuilder line = new PeppolMLSLineResponseBuilder("1")
                .addResponse("Business rule violation fatal", MLSStatusReasonCode.BV);

        // Field derived from the original message: its SBDH InstanceIdentifier.
        String originalSbdhInstanceIdentifier = UUID.randomUUID().toString();

        // Create the builder - this also configures sender/receiver (constructor arguments) and
        // stamps the fixed MLS 1.0 profile (CustomizationID/ProfileID) automatically.
        ApplicationResponseType mls = new PeppolMLSBuilder(SENDER_ID, SCHEME_ID, RECEIVER_ID, SCHEME_ID)
                // Explicit identifier/date/time overrides are optional -
                // if omitted, PeppolMLSBuilder defaults to a random UUID and LocalDate.now()/OffsetTime.now().
                .withIdMLS(() -> "MLS-" + UUID.randomUUID())
                .withIssueDateMLS(() -> LocalDate.of(2026, 8, 31))
                .withIssueTimeMLS(() -> OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC))
                // Original message reference + document-level status.
                .setDocumentResponse(originalSbdhInstanceIdentifier, MLSResponseCode.RE.getCode(), "Rejected")
                // Attach the line-level status built above.
                .addLineResponse(line.build())
                // build() runs PeppolMLSIntegrityValidator.validate(...) - internally - an incomplete response never leaves the builder.
                .build();

        assertNotNull(mls);

        // Serialize to XML. marshalToString() re-validates before writing.
        String xml = PeppolMLSMarshaller.marshalToString(mls);

        System.out.println(xml);

        assertTrue(xml.matches("(?s).*<.*ResponseCode>RE</.*ResponseCode>.*"));
        assertTrue(xml.matches("(?s).*<.*StatusReasonCode>BV</.*StatusReasonCode>.*"));
        assertTrue(xml.contains(originalSbdhInstanceIdentifier));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void buildingWithoutDocumentResponseFails() {
        // setDocumentResponse(...) is mandatory - build() enforces this with an IllegalStateException("DocumentResponse must be set").
        new PeppolMLSBuilder(SENDER_ID, SCHEME_ID, RECEIVER_ID, SCHEME_ID).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void blankSenderIdIsRejectedImmediately() {
        // Sender/receiver ID and schemeID are validated eagerly in the constructor, before any other builder method can even be called.
        new PeppolMLSBuilder("", SCHEME_ID, RECEIVER_ID, SCHEME_ID);
    }
}
