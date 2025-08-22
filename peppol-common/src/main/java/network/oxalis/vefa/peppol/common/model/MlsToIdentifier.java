package network.oxalis.vefa.peppol.common.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

public class MlsToIdentifier extends AbstractQualifiedIdentifier implements Serializable {

    private static final long serialVersionUID = 2384671903287416598L;

    /**
     * Default scheme used when no scheme specified.
     */
    public static final Scheme DEFAULT_SCHEME = Scheme.of("iso6523-actorid-upis");

    public static MlsToIdentifier of(String value) {
        return of(value, DEFAULT_SCHEME);
    }

    public static MlsToIdentifier of(String value, Scheme scheme) {
        return new MlsToIdentifier(value, scheme);
    }

    /**
     * Creation of participant identifier.
     *
     * @param identifier Normal identifier like '0242:000723'.
     * @param scheme     Scheme for identifier.
     */
    private MlsToIdentifier(String identifier, Scheme scheme) {
        super(identifier.trim().toLowerCase(Locale.US), scheme);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MlsToIdentifier that = (MlsToIdentifier) o;

        if (!identifier.equals(that.identifier)) return false;
        return scheme.equals(that.scheme);

    }

    @Override
    public int hashCode() {
        return Objects.hash(scheme, identifier);
    }

    @Override
    public String toString() {
        return scheme + "::" + identifier;
    }
}
