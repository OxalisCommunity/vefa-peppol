package no.difi.vefa.peppol.common.model;

import lombok.Getter;
import no.difi.vefa.peppol.common.api.SimpleIdentifier;

/**
 * @author erlend
 */
@Getter
public class ArgumentIdentifier implements SimpleIdentifier {

    private String key;

    private String identifier;

    public static ArgumentIdentifier of(String key, String value) {
        return new ArgumentIdentifier(key, value);
    }

    protected ArgumentIdentifier(String key, String identifier) {
        this.key = key != null ? key.trim() : null;
        this.identifier = identifier != null ? identifier.trim() : null;
    }
}
