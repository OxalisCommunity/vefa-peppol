package no.difi.vefa.peppol.security.api;

import no.difi.vefa.peppol.common.lang.PeppolException;

public class PeppolSecurityException extends PeppolException {

    private static final long serialVersionUID = 6928682319726226728L;

    public PeppolSecurityException(String message) {
        super(message);
    }

    public PeppolSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
