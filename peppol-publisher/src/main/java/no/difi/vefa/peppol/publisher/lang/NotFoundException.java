package no.difi.vefa.peppol.publisher.lang;

/**
 * @author erlend
 */
public class NotFoundException extends PublisherException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
