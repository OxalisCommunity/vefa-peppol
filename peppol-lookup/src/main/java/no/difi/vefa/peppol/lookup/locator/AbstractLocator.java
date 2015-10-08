package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

import java.net.URI;

public abstract class AbstractLocator implements MetadataLocator {

    @Override
    public URI lookup(String identifier) throws LookupException {
        return lookup(new ParticipantIdentifier(identifier));
    }
}
