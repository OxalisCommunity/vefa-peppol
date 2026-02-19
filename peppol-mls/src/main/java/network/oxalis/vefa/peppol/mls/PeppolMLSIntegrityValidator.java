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
import network.oxalis.peppol.ubl2.jaxb.cac.*;

public final class PeppolMLSIntegrityValidator {

    private PeppolMLSIntegrityValidator() {
    }

    public static void validate(ApplicationResponseType mls) {

        if (mls == null) {
            throw new IllegalStateException("MLS cannot be null");
        }

        require(mls.getCustomizationID(), "CustomizationID is required");
        require(mls.getProfileID(), "ProfileID is required");
        require(mls.getID(), "ID is required");
        require(mls.getIssueDate(), "IssueDate is required");
        require(mls.getIssueTime(), "IssueTime is required");

        validateParty(mls.getSenderParty(), "SenderParty");
        validateParty(mls.getReceiverParty(), "ReceiverParty");

        if (mls.getDocumentResponse() == null ||
                mls.getDocumentResponse().size() != 1) {
            throw new IllegalStateException("Exactly one DocumentResponse is required");
        }

        DocumentResponseType dr = mls.getDocumentResponse().get(0);
        if (dr.getDocumentReference() == null ||
                dr.getDocumentReference().size() != 1) {
            throw new IllegalStateException("Exactly one DocumentReference is required");
        }

        DocumentReferenceType documentReference = dr.getDocumentReference().get(0);

        require(documentReference.getID(),
                "DocumentReference ID is required");

        ResponseType documentResponse = dr.getResponse();
        require(documentResponse, "Document-level Response is required");
        require(documentResponse.getResponseCode(),
                "ResponseCode is required");

        if (documentResponse.getDescription() != null &&
                documentResponse.getDescription().size() > 1) {
            throw new IllegalStateException(
                    "Document-level Description must not occur more than once");
        }

        if (dr.getLineResponse() != null) {

            for (LineResponseType lr : dr.getLineResponse()) {

                require(lr.getLineReference(),
                        "LineReference is required");

                require(lr.getLineReference().getLineID(),
                        "LineID is required");

                if (lr.getResponse() == null ||
                        lr.getResponse().isEmpty()) {
                    throw new IllegalStateException(
                            "Each LineResponse must contain at least one Response");
                }

                for (ResponseType r : lr.getResponse()) {

                    if (r.getDescription() == null ||
                            r.getDescription().size() != 1) {
                        throw new IllegalStateException(
                                "Line-level Response must contain exactly one Description");
                    }

                    if (r.getStatus() == null ||
                            r.getStatus().size() != 1) {
                        throw new IllegalStateException(
                                "Line-level Response must contain exactly one Status");
                    }

                    StatusType status = r.getStatus().get(0);

                    require(status.getStatusReasonCode(),
                            "StatusReasonCode is required in Status");
                }
            }
        }
    }

    private static void validateParty(PartyType party, String label) {

        require(party, label + " is required");
        require(party.getEndpointID(),
                label + " EndpointID is required");
        require(party.getEndpointID().getSchemeID(),
                label + " EndpointID schemeID is required");
        require(party.getEndpointID().getValue(),
                label + " EndpointID value is required");
    }

    private static void require(Object value, String message) {
        if (value == null) {
            throw new IllegalStateException(message);
        }
    }
}

