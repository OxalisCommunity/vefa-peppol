package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class BusdoxLocator extends AbstractLocator {

    private String hostname;

    public BusdoxLocator() {
        this(DynamicLocator.OPENPEPPOL_PRODUCTION);
    }

    public BusdoxLocator(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        try {
            String receiverHash = DigestUtils.md5Hex(participantIdentifier.getIdentifier().getBytes(StandardCharsets.UTF_8));
            return new URI(String.format("http://B-%s.%s.%s", receiverHash, participantIdentifier.getScheme(), hostname));
        } catch (URISyntaxException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
