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

/**
 * Valid event identifiers according to ETSI TS 102 640-2 V2.1.1, section B.1.1
 *
 * @author steinar
 *         Date: 04.11.2015
 *         Time: 18.22
 */
public enum EventCode {

    // No "//" after "http:" as specified in specification.
    ACCEPTANCE("http:uri.etsi.org/02640/Event#Acceptance"),
    REJECTION("http:uri.etsi.org/02640/Event#Rejection"),
    DELIVERY("http:uri.etsi.org/02640/Event#Delivery"),
    DELIVERY_EXPIRATION("http:uri.etsi.org/02640/Event#DeliveryExpiration");

    private final String value;

    EventCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventCode valueFor(String value) {
        for (EventCode eventCode : values())
            if (eventCode.value.equals(value))
                return eventCode;

        throw new IllegalArgumentException(String.format("Value '%s' does not represent a valid EventCode", value));
    }
}
