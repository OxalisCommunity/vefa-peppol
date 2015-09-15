package no.difi.vefa.peppol.lookup.model;

import java.io.Serializable;

public class ProcessIdentifier implements Serializable {

    private static final long serialVersionUID = 7486398061021950763L;

    private String identifier;
    private String scheme;

    public ProcessIdentifier(String identifier, String scheme) {
        this.identifier = identifier;
        this.scheme = scheme;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getScheme() {
        return scheme;
    }
}
