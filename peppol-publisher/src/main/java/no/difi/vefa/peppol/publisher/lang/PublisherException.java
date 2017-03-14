package no.difi.vefa.peppol.publisher.lang;

/**
 * @author erlend
 */
public abstract class PublisherException extends Exception {

    public PublisherException(String message) {
        super(message);
    }

    public PublisherException(String message, Throwable cause) {
        super(message, cause);
    }
}
