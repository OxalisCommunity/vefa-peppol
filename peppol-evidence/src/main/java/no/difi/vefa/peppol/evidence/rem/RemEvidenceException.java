package no.difi.vefa.peppol.evidence.rem;


public class RemEvidenceException extends Exception {

    private static final long serialVersionUID = 8324791890166619197L;

    public RemEvidenceException(String message) {
        super(message);
    }
    
    public RemEvidenceException(String message, Exception ex) {
        super(message,ex);
    }
    
    public RemEvidenceException(Exception ex) {
        super(ex);
    }
}
