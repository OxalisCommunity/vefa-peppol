/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
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

    String value;

    EventCode(String uri) {
        value = uri;
    }

    public String getValue() {
        return value;
    }

    public static EventCode valueFor(String valueToLookup) {
        if (valueToLookup == null)
            throw new IllegalArgumentException("null is invalid argument!");

        for (EventCode eventCode : values())
            if (eventCode.value.equals(valueToLookup))
                return eventCode;

        throw new IllegalArgumentException(valueToLookup + " does not represent a valid EventCode");
    }
}
