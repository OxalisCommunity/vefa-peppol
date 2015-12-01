package no.difi.vefa.peppol.common.api;

import no.difi.vefa.peppol.common.lang.PeppolException;

public interface Verifier<T> {

    void verify(T o) throws PeppolException;

}
