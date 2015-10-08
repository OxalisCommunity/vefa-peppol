package no.difi.vefa.peppol.security.api;

import no.difi.vefa.peppol.common.api.PeppolException;

public class SecurityException extends PeppolException {

    private static final long serialVersionUID = 6928682319726226728L;

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
