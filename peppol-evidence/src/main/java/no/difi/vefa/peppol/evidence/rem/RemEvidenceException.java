package no.difi.vefa.peppol.evidence.rem;


import no.difi.vefa.peppol.common.lang.PeppolException;

public class RemEvidenceException extends PeppolException {

    private static final long serialVersionUID = 8324791890166619197L;

    public RemEvidenceException(String message) {
        super(message);
    }

    public RemEvidenceException(String message, Exception ex) {
        super(message, ex);
    }
}
