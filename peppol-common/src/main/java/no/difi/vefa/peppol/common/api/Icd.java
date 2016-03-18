package no.difi.vefa.peppol.common.api;

import no.difi.vefa.peppol.common.model.Scheme;

public interface Icd {

    String getIdentifier();

    String getCode();

    Scheme getScheme();

    boolean isDeprecated();

}
