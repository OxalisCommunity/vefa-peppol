package no.difi.vefa.peppol.common.util;

import no.difi.vefa.peppol.common.api.Verifier;
import no.difi.vefa.peppol.common.lang.PeppolException;

import java.util.ArrayList;
import java.util.List;

public class VerifierCollection {

    private List<Verifier<?>> verifiers = new ArrayList<Verifier<?>>();

    public void add(Verifier<?> verifier) {
        verifiers.add(verifier);
    }

    public void verify(Object o) throws PeppolException {
        Class<?> cls = o.getClass();
    }

    public void clear() {
        verifiers.clear();
    }

    public void clear(Class<?> cls) {
        for (Verifier<?> verifier : verifiers) {

        }
    }
}
