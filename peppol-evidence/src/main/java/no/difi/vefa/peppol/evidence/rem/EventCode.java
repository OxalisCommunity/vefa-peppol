package no.difi.vefa.peppol.evidence.rem;

import java.net.URI;
import java.net.URISyntaxException;

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
    DELIVERY_EXPIRATION("http:uri.etsi.org/02640/Event#DeliveryExpiration")
    ;

    URI value;

    EventCode(String uri) {
        try {
            value = new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI for EventCode: " + uri);
        }
    }

    public URI getValue() {
        return value;
    }

    public static EventCode valueFor(String valueToLookup){
        if (valueToLookup == null) {
            throw new IllegalArgumentException("null is invalid argument!");
        }
        try {
            URI uri = new URI(valueToLookup);
            for (EventCode eventCode : values()) {
                if (eventCode.getValue().toString().equals(uri.toString())) {
                    return eventCode;
                }
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(valueToLookup + " does not represent a valid URI " + e.getMessage(), e);
        }
        throw new IllegalArgumentException(valueToLookup + " does not represent a valid EventCode");
    }
}
