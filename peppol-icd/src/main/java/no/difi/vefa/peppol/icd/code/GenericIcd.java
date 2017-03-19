package no.difi.vefa.peppol.icd.code;

import no.difi.vefa.peppol.common.model.Scheme;
import no.difi.vefa.peppol.icd.api.Icd;

/**
 * @author erlend
 */
public class GenericIcd implements Icd {

    private final String identifier;

    private final String code;

    private final Scheme scheme;

    public static Icd of(String identifier, String code, Scheme scheme) {
        return new GenericIcd(identifier, code, scheme);
    }

    private GenericIcd(String identifier, String code, Scheme scheme) {
        this.identifier = identifier;
        this.code = code;
        this.scheme = scheme;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Scheme getScheme() {
        return scheme;
    }
}
