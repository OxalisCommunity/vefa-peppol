package no.difi.vefa.peppol.lookup.locator;

import com.google.common.io.BaseEncoding;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.util.DynamicHostnameGenerator;
import org.xbill.DNS.*;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of Business Document Metadata Service Location Version 1.0.
 *
 * @see <a href="http://docs.oasis-open.org/bdxr/BDX-Location/v1.0/BDX-Location-v1.0.html">Specification</a>
 */
public class BdxlLocator extends AbstractLocator {

    private DynamicHostnameGenerator hostnameGenerator;

    /**
     * Initiate a new instance of BDXL lookup functionality using SHA-224 for hashing.
     *
     * @param hostname Hostname used as base for lookup.
     */
    public BdxlLocator(String hostname) {
        this(hostname, "SHA-256");
    }

    /**
     * Initiate a new instance of BDXL lookup functionality.
     *
     * @param hostname        Hostname used as base for lookup.
     * @param digestAlgorithm Algorithm used for generation of hostname.
     */
    public BdxlLocator(String hostname, String digestAlgorithm) {
        this("", hostname, digestAlgorithm);
    }

    /**
     * Initiate a new instance of BDXL lookup functionality.
     *
     * @param prefix          Value attached in front of calculated hash.
     * @param hostname        Hostname used as base for lookup.
     * @param digestAlgorithm Algorithm used for generation of hostname.
     */
    public BdxlLocator(String prefix, String hostname, String digestAlgorithm) {
        this(prefix, hostname, digestAlgorithm, BaseEncoding.base32());
    }

    /**
     * Initiate a new instance of BDXL lookup functionality.
     *
     * @param prefix          Value attached in front of calculated hash.
     * @param hostname        Hostname used as base for lookup.
     * @param digestAlgorithm Algorithm used for generation of hostname.
     * @param encoding        Encoding of hash for hostname.
     */
    public BdxlLocator(String prefix, String hostname, String digestAlgorithm, BaseEncoding encoding) {
        hostnameGenerator = new DynamicHostnameGenerator(prefix, hostname, digestAlgorithm, encoding);
    }

    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        // Create hostname for participant identifier.
        String hostname = hostnameGenerator.generate(participantIdentifier).replaceAll("=*", "");

        try {
            // Fetch all records of type NAPTR registered on hostname.
            Record[] records = new Lookup(hostname, Type.NAPTR).run();
            if (records == null)
                throw new LookupException(String.format("Identifier '%s' not registered in SML.", participantIdentifier.toString()));

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

        throw new LookupException("Record for SMP not found in SML.");
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
