package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.model.ParticipantIdentifier;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class BusdoxLocator extends AbstractLocator {

    public static final String OPENPEPPOL_PRODUCTION = "edelivery.tech.ec.europa.eu";
    public static final String OPENPEPPOL_TEST = "acc.edelivery.tech.ec.europa.eu";

    private String hostname;

    public BusdoxLocator() {
        this(OPENPEPPOL_PRODUCTION);
    }

    public BusdoxLocator(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        try {
            String receiverHash = DigestUtils.md5Hex(participantIdentifier.getIdentifier());
            return new URI(String.format("http://b-%s.%s.%s", receiverHash, participantIdentifier.getScheme(), hostname));
        } catch (URISyntaxException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
