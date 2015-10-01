package no.difi.vefa.edelivery.lookup.locator;

import no.difi.vefa.edelivery.lookup.api.LookupException;
import no.difi.vefa.edelivery.lookup.model.ParticipantIdentifier;

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
