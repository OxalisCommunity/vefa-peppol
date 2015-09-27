package no.difi.vefa.edelivery.lookup.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ParticipantIdentifier implements Serializable {

    private static final long serialVersionUID = -8052874032415088055L;

    private String identifier;
    private String scheme;

    public ParticipantIdentifier(String identifier, String scheme) {
        this.identifier = identifier.trim().toLowerCase();
        this.scheme = scheme;
    }

    public ParticipantIdentifier(String identifier) {
        this(identifier, "iso6523-actorid-upis");
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getScheme() {
        return scheme;
    }

    public String urlencoded() {
        try {
            return URLEncoder.encode(String.format("%s::%s", scheme, identifier), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipantIdentifier that = (ParticipantIdentifier) o;

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
