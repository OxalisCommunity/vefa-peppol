package network.oxalis.vefa.peppol.common.model;

import java.io.Serializable;
import java.util.Objects;

public class MlsTypeIdentifier extends AbstractQualifiedIdentifier implements Serializable {

    private static final long serialVersionUID = -6438291745328092614L;

    public static MlsTypeIdentifier of(String identifier) {
        return new MlsTypeIdentifier(identifier);
    }

    /**
     * Creates a MlsTypeIdentifier: MLS_TYPE - The identifier scheme is always null for this identifier type.
     */
    public MlsTypeIdentifier(String identifier) {
        super(identifier, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MlsTypeIdentifier that = (MlsTypeIdentifier) o;
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
