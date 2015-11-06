package no.difi.vefa.peppol.common.lang;

public class PeppolException extends Exception {

    private static final long serialVersionUID = 5107173982482489878L;

    public PeppolException(String message) {
        super(message);
    }

    public PeppolException(String message, Throwable cause) {
        super(message, cause);
    }

    public PeppolException(Throwable cause) {
        super(cause);
    }
}
