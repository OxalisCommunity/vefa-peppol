package no.difi.vefa.edelivery.lookup.api;

public class EDeliveryException extends Exception {

    private static final long serialVersionUID = 5107173982482489878L;

    public EDeliveryException(String message) {
        super(message);
    }

    public EDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EDeliveryException(Throwable cause) {
        super(cause);
    }
}
