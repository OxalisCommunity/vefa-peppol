package no.difi.vefa.peppol.icd.api;

import no.difi.vefa.peppol.common.model.Scheme;

public interface Icd {

    String getIdentifier();

    String getCode();

    Scheme getScheme();

}
