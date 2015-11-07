package no.difi.vefa.peppol.common.api;

import no.difi.vefa.peppol.common.model.Scheme;

public interface Icd {
    String getIdentifier();

    String getIcd();

    Scheme getScheme();

    boolean isDeprecated();
}
