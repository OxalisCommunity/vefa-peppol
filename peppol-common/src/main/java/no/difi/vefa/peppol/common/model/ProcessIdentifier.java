package no.difi.vefa.peppol.common.model;

import java.io.Serializable;

public class ProcessIdentifier implements Serializable {

    private static final long serialVersionUID = 7486398061021950763L;

    private String identifier;
    private Scheme scheme;

    public ProcessIdentifier(String identifier) {
        this(identifier, new Scheme("cenbii-procid-ubl"));
    }

    @Deprecated
    public ProcessIdentifier(String identifier, String scheme) {
        this(identifier, new Scheme(scheme));
    }

    public ProcessIdentifier(String identifier, Scheme scheme) {
        this.identifier = identifier;
        this.scheme = scheme;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Scheme getScheme() {
        return scheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessIdentifier that = (ProcessIdentifier) o;

        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) return false;
        return !(scheme != null ? !scheme.equals(that.scheme) : that.scheme != null);

    }

    @Override
    public int hashCode() {
        int result = identifier != null ? identifier.hashCode() : 0;
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        return result;
    }
}
