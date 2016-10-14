package no.difi.vefa.peppol.common.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Representation of a participant identifier. Immutable object.
 */
public class ParticipantIdentifier implements Serializable {

    private static final long serialVersionUID = -8052874032415088055L;

    /**
     * Default scheme used when no scheme or ICD specified.
     */
    public static final Scheme DEFAULT_SCHEME = Scheme.of("iso6523-actorid-upis");

    private Scheme scheme;

    private String identifier;

    public static ParticipantIdentifier of(String identifier) {
        return of(identifier, DEFAULT_SCHEME);
    }

    public static ParticipantIdentifier of(String identifier, Scheme scheme) {
        return new ParticipantIdentifier(identifier, scheme);
    }

    /**
     * Creation of identifier based on ISO6523 as defined by OpenPEPPOL.
     *
     * @param identifier Normal identifier like '9908:987654321'.
     */
    public ParticipantIdentifier(String identifier) {
        this(identifier, DEFAULT_SCHEME);
    }

    /**
     * Creation of participant identifier.
     *
     * @param identifier Normal identifier like '9908:987654321'.
     * @param scheme     Scheme for identifier.
     */
    public ParticipantIdentifier(String identifier, Scheme scheme) {
        this.identifier = identifier.trim().toLowerCase(Locale.US);
        this.scheme = scheme;
    }

    /**
     * Identifier of participant.
     *
     * @return Identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Scheme of ICD.
     *
     * @return Scheme.
     */
    public Scheme getScheme() {
        return scheme;
    }

    /**
     * Returns full identifier, like 'iso6523-actorid-upis::9908:987654321'.
     *
     * @return URL-encoded full identifier.
     */
    public String urlencoded() {
        try {
            return URLEncoder.encode(
                    String.format("%s::%s", scheme.getValue(), identifier), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipantIdentifier that = (ParticipantIdentifier) o;

        if (!scheme.equals(that.scheme)) return false;
        return identifier.equals(that.identifier);

    }

    @Override
    public int hashCode() {
        int result = scheme.hashCode();
        result = 31 * result + identifier.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s::%s", scheme, identifier);
    }
}
