package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;

import java.net.URI;

public class DynamicLocator extends AbstractLocator {

    public static final String OPENPEPPOL_PRODUCTION = "edelivery.tech.ec.europa.eu";

    public static final String OPENPEPPOL_TEST = "acc.edelivery.tech.ec.europa.eu";

    private MetadataLocator locator;

    public DynamicLocator(String hostname) {
        locator = OPENPEPPOL_TEST.equals(hostname) ? new BdxlLocator(hostname) : new BusdoxLocator(hostname);
    }

    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        return locator.lookup(participantIdentifier);
    }
}
