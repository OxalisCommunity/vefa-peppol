package network.oxalis.vefa.peppol.common.model;

import java.io.Serializable;

public class C1CountryIdentifier extends AbstractSimpleIdentifier implements Serializable {

    private static final long serialVersionUID = -3492824463587521885L;

    public static C1CountryIdentifier of(String value) {
        return new C1CountryIdentifier(value);
    }

    public C1CountryIdentifier(String value) {
        super(value);
    }

}
