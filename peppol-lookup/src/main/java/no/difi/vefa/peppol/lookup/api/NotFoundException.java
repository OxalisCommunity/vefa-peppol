package no.difi.vefa.peppol.lookup.api;

public class NotFoundException extends LookupException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
