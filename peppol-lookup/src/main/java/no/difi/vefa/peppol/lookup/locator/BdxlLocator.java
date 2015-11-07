package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.LookupException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.xbill.DNS.*;

import java.net.URI;
import java.security.MessageDigest;
import java.security.Security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of Business Document Metadata Service Location Version 1.0.
 *
 * @see <a href="http://docs.oasis-open.org/bdxr/BDX-Location/v1.0/BDX-Location-v1.0.html">Specification</a>
 */
public class BdxlLocator extends AbstractLocator {

    static {
        // Make sure to register Bouncy Castle as a provider.
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Base hostname for lookup.
     */
    private String hostname;
    /**
     * Algorithm used for geneation of hostname.
     */
    private String digestAlgorithm;

    /**
     * Initiate a new instance of BDXL lookup functionality using SHA-224 for hashing.
     *
     * @param hostname Hostname used as base for lookup.
     */
    public BdxlLocator(String hostname) {
        this(hostname, "SHA-224");
    }

    /**
     * Initiate a new instance of BDXL lookup functionality.
     *
     * @param hostname Hostname used as base for lookup.
     * @param digestAlgorithm Algorithm used for generation of hostname.
     */
    public BdxlLocator(String hostname, String digestAlgorithm) {
        this.hostname = hostname;
        this.digestAlgorithm = digestAlgorithm;
    }

    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {

        String receiverHash;

        try {
            // Create digest based on participant identifier.
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
            byte[] digest = md.digest(participantIdentifier.toString().getBytes());

            // Create hex of digest.
            receiverHash = Hex.encodeHexString(digest);
        } catch (Exception e) {
            throw new LookupException(e.getMessage(), e);
        }

        // Create hostname for participant identifier.
        String hostname = String.format("B-%s.%s.%s", receiverHash, participantIdentifier.getScheme().getValue(), this.hostname);

        try {
            // Fetch all records of type NAPTR registered on hostname.
            Record[] records = new Lookup(hostname, Type.NAPTR).run();
            if (records == null)
                return null;

            // Loop records found.
            for (Record record : records) {
                // Simple cast.
                NAPTRRecord naptrRecord = (NAPTRRecord) record;

                // Handle only those having "Meta:SMP" as service.
                if ("Meta:SMP".equals(naptrRecord.getService()) && "U".equalsIgnoreCase(naptrRecord.getFlags())) {

                    // Create URI and return.
                    String result = handleRegex(naptrRecord.getRegexp(), hostname);
                    if (result != null)
                        return URI.create(result);
                }
            }
        } catch (TextParseException e) {
            throw new LookupException("Error when handling DNS lookup for BDXL.", e);
        }

        return null;
    }

    public static String handleRegex(String naptrRegex, String hostname) {
        String[] regexp = naptrRegex.split("!");

        // Simple stupid
        if ("^.*$".equals(regexp[1]))
            return regexp[2];

        // Using regex
        Pattern pattern = Pattern.compile(regexp[1]);
        Matcher matcher = pattern.matcher(hostname);
        if (matcher.matches())
            return matcher.replaceAll(regexp[2].replaceAll("\\\\{2}", "\\$"));

        // No match
        return null;
    }
}
