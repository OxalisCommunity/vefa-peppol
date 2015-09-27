package no.difi.vefa.edelivery.lookup.api;

public class LookupException extends EDeliveryException {

    private static final long serialVersionUID = -8630614964594045904L;

    public LookupException(String message) {
        super(message);
    }

    public LookupException(String message, Throwable cause) {
        super(message, cause);
    }

    public LookupException(Throwable cause) {
        super(cause);
    }
}
