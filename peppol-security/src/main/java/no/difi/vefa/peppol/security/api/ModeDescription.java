package no.difi.vefa.peppol.security.api;

import no.difi.vefa.peppol.common.code.Service;

import java.io.InputStream;

public interface ModeDescription {

    String getIdentifier();

    String[] getIssuers(Service service);

    InputStream getKeystore();

}
