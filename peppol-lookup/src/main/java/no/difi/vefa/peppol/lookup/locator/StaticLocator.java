package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

import java.net.URI;

public class StaticLocator extends AbstractLocator {

    private URI defaultUri;

    public StaticLocator(URI defaultUri) {
        this.defaultUri = defaultUri;
    }

    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        return defaultUri;
    }
}
