package no.difi.vefa.edelivery.lookup.api;

public class SecurityException extends EDeliveryException {

    private static final long serialVersionUID = 6928682319726226728L;

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityException(Throwable cause) {
        super(cause);
    }
}
