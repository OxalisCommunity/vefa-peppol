package no.difi.vefa.peppol.lookup.model;

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
}
