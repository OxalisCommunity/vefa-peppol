package no.difi.vefa.edelivery.lookup.locator;

import no.difi.vefa.edelivery.lookup.api.LookupException;
import no.difi.vefa.edelivery.lookup.api.MetadataLocator;
import no.difi.vefa.edelivery.lookup.model.ParticipantIdentifier;

import java.net.URI;

public abstract class AbstractLocator implements MetadataLocator {

    @Override
    public URI lookup(String identifier) throws LookupException {
        return lookup(new ParticipantIdentifier(identifier));
    }
}
