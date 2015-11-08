package no.difi.vefa.peppol.lookup.util;

import com.google.common.io.BaseEncoding;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.LookupException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;

public class DynamicHostnameGenerator {

    static {
        // Make sure to register Bouncy Castle as a provider.
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    private static BaseEncoding hexEncoding = BaseEncoding.base16();

    /**
     * Prefix for generated hostname.
     */
    private String prefix;

    /**
     * Base hostname for lookup.
     */
    private String hostname;

    /**
     * Algorithm used for geneation of hostname.
     */
    private String digestAlgorithm;

    public DynamicHostnameGenerator(String prefix, String hostname, String digestAlgorithm) {
        this.prefix = prefix;
        this.hostname = hostname;
        this.digestAlgorithm = digestAlgorithm;
    }

    public String generate(ParticipantIdentifier participantIdentifier) throws LookupException {
        String receiverHash;

        try {
            // Create digest based on participant identifier.
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
            byte[] digest = md.digest(participantIdentifier.toString().getBytes(StandardCharsets.UTF_8));

            // Create hex of digest.
            receiverHash = hexEncoding.encode(digest).toLowerCase();
        } catch (Exception e) {
            throw new LookupException(e.getMessage(), e);
        }

        return String.format("%s%s.%s.%s", prefix, receiverHash, participantIdentifier.getScheme().getValue(), hostname);
    }
}
