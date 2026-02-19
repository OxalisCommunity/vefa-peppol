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
import network.oxalis.peppol.ubl2.jaxb.cbc.DescriptionType;
import network.oxalis.peppol.ubl2.jaxb.cbc.LineIDType;
import network.oxalis.peppol.ubl2.jaxb.cbc.StatusReasonCodeType;

public class PeppolMLSLineResponseBuilder {

    private final LineResponseType lineResponse = new LineResponseType();
    private boolean lineReferenceSet = false;
    private boolean hasResponse = false;

    public PeppolMLSLineResponseBuilder(String lineId) {

        validateNotBlank(lineId, "LineID is required");

        LineReferenceType lineReference = new LineReferenceType();
        LineIDType lineIDType = new LineIDType();
        lineIDType.setValue(lineId);
        lineReference.setLineID(lineIDType);

        lineResponse.setLineReference(lineReference);
        lineReferenceSet = true;
    }

    /**
     * MLS cardinality: 1..n
     */
    public PeppolMLSLineResponseBuilder addResponse(
            String description,
            MLSStatusReasonCode reasonCode) {

        validateNotBlank(description, "Description is required");

        if (reasonCode == null) {
            throw new IllegalArgumentException("StatusReasonCode is required");
        }

        ResponseType response = new ResponseType();

        DescriptionType descriptionType = new DescriptionType();
        descriptionType.setValue(description);
        response.getDescription().add(descriptionType);

        StatusType status = new StatusType();

        StatusReasonCodeType code = new StatusReasonCodeType();
        code.setValue(reasonCode.getCode());
        status.setStatusReasonCode(code);

        response.getStatus().add(status);

        lineResponse.getResponse().add(response);
        hasResponse = true;

        return this;
    }

    public LineResponseType build() {

        if (!hasResponse) {
            throw new IllegalStateException(
                    "At least one Response is required per LineResponse");
        }

        return lineResponse;
    }

    private void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
