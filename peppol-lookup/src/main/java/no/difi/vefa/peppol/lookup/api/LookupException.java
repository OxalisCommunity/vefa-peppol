package no.difi.vefa.peppol.lookup.api;

import no.difi.vefa.peppol.common.lang.PeppolException;

public class LookupException extends PeppolException {

    private static final long serialVersionUID = -8630614964594045904L;

    public LookupException(String message) {
        super(message);
    }

    public LookupException(String message, Throwable cause) {
        super(message, cause);
    }
}
