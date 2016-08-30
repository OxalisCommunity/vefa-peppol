package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.lang.PeppolException;

import java.io.Serializable;
import java.util.regex.Pattern;

public class TransportProtocol implements Serializable {

    private static final long serialVersionUID = -5938766453542971103L;

    private static Pattern pattern = Pattern.compile("[\\p{Upper}\\d]+");

    public static final TransportProtocol AS2 = new TransportProtocol("AS2");

    public static final TransportProtocol AS4 = new TransportProtocol("AS4");

    public static final TransportProtocol INTERNAL = new TransportProtocol("INTERNAL");

    public static TransportProtocol forIdentifier(String identifier) throws PeppolException {
        if (!pattern.matcher(identifier).matches())
            throw new PeppolException("Identifier not according to pattern.");

        return new TransportProtocol(identifier);
    }

    private String identifier;

    private TransportProtocol(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransportProtocol that = (TransportProtocol) o;

        return identifier.equals(that.identifier);

    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "TransportProtocol{" + identifier + '}';
    }
}
