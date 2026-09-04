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
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ReadMLSIntTest extends AbstractPeppolTest {

    @Test
    public void readMlsApplicationResponseFromXml() throws Exception {

        // Load MLS XML
        String xml = loadXml("/mls/good/MessageLevelStatus_Example_RE.xml");

        // Parse/deserialize using the actual peppol-mls API.
        ApplicationResponseType mls = PeppolMLSMarshaller.unmarshal(xml);
        assertNotNull(mls);

        // unmarshal() performs no structural validation by itself
        PeppolMLSIntegrityValidator.validate(mls);

        // Read sender information.
        String senderId = mls.getSenderParty().getEndpointID().getValue();
        String senderScheme = mls.getSenderParty().getEndpointID().getSchemeID();

        // Read receiver information.
        String receiverId = mls.getReceiverParty().getEndpointID().getValue();
        String receiverScheme = mls.getReceiverParty().getEndpointID().getSchemeID();

        // Read the (single, mandatory) document-level response.
        DocumentResponseType documentResponse = mls.getDocumentResponse().get(0);
        String responseCode = documentResponse.getResponse().getResponseCode().getValue();
        String responseDescription = documentResponse.getResponse().getDescription().isEmpty()
                ? null
                : documentResponse.getResponse().getDescription().get(0).getValue();

        // Read the original message/document reference (the SBDH InstanceIdentifier of the message this MLS response is about).
        String originalInstanceId = documentResponse.getDocumentReference().get(0).getID().getValue();

        // Read identifiers and date/time of the MLS response itself.
        String mlsId = mls.getID().getValue();
        String issueDate = mls.getIssueDate().getValue().toString();
        String issueTime = mls.getIssueTime().getValue().toString();

        // Read line-level status, if present.
        for (LineResponseType lineResponse : documentResponse.getLineResponse()) {
            String lineId = lineResponse.getLineReference().getLineID().getValue();
            for (var lineLevelResponse : lineResponse.getResponse()) {
                String lineDescription = lineLevelResponse.getDescription().get(0).getValue();
                String reasonCode = lineLevelResponse.getStatus().get(0).getStatusReasonCode().getValue();
                System.out.println("Line " + lineId + ": " + reasonCode + " - " + lineDescription);
            }
        }

        System.out.println("MLS ID: " + mlsId);
        System.out.println("Issued: " + issueDate + "T" + issueTime);
        System.out.println("Sender: " + senderScheme + ":" + senderId);
        System.out.println("Receiver: " + receiverScheme + ":" + receiverId);
        System.out.println("Response code: " + responseCode + " (" + responseDescription + ")");
        System.out.println("Original message ID: " + originalInstanceId);

        // Sanity assertions
        assertEquals(responseCode, "RE");
        assertEquals(senderId, "000723");
        assertEquals(receiverId, "000010");
        assertFalse(documentResponse.getLineResponse().isEmpty());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void unmarshalOfMalformedXmlThrowsRuntimeException() {
        // PeppolMLSMarshaller wraps every unmarshal failure (not-well-formed XML, JAXB binding errors, ...)
        // into an unchecked RuntimeException("Failed to unmarshal MLS", cause).
        PeppolMLSMarshaller.unmarshal("<ApplicationResponse><UnclosedElement>");
    }
}
