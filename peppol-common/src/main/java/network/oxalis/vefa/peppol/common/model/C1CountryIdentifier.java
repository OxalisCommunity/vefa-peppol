package network.oxalis.vefa.peppol.common.model;

import java.io.Serializable;
import java.util.Objects;

public class C1CountryIdentifier extends AbstractQualifiedIdentifier implements Serializable {

    private static final long serialVersionUID = -3492824463587521885L;

    public static C1CountryIdentifier of(String identifier) {
        return new C1CountryIdentifier(identifier);
    }

    /**
     * Creates a C1CountryIdentifier. The identifier scheme is always null for this identifier type.
     */
    public C1CountryIdentifier(String value) {
        super(value, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        C1CountryIdentifier that = (C1CountryIdentifier) o;
        return Objects.equals(getIdentifier(), that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}

