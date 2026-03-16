/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package network.oxalis.vefa.peppol.lookup.locator;

import com.google.common.io.BaseEncoding;
import lombok.extern.slf4j.Slf4j;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.PeppolInfrastructureException;
import network.oxalis.vefa.peppol.lookup.api.PeppolResourceException;
import network.oxalis.vefa.peppol.lookup.util.DynamicHostnameGenerator;
import network.oxalis.vefa.peppol.lookup.util.EncodingUtils;
import network.oxalis.vefa.peppol.mode.Mode;
import org.apache.commons.lang3.StringUtils;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of Business Document Metadata Service Location Version 1.0.
 *
 * @see <a href="http://docs.oasis-open.org/bdxr/BDX-Location/v1.0/BDX-Location-v1.0.html">Specification</a>
 *
 *
 * <p>DNS result codes are mapped to domain-specific exceptions at this layer.
 * Unlike the HTTP/SMP layer ({@link network.oxalis.vefa.peppol.lookup.fetcher.ApacheFetcher}),
 * DNS is the native protocol here, so result codes carry definitive meaning:</p>
 * <ul>
 *   <li>HOST_NOT_FOUND / TYPE_NOT_FOUND → {@link PeppolResourceException}
 *       — participant definitively not in SML</li>
 *   <li>TRY_AGAIN → {@link PeppolInfrastructureException}
 *       — transient DNS failure, retry may help</li>
 *   <li>UNRECOVERABLE → {@link PeppolInfrastructureException}
 *       — SML DNS server error</li>
 * </ul>
 * @see PeppolResourceException
 * @see PeppolInfrastructureException
 */

@Slf4j
public class BdxlLocator extends AbstractLocator {

    private final long timeout;
    private final int maxRetries;
    private final boolean enablePublicDNS;

    private static final List<InetAddress> customDNSServers = new ArrayList<>();
    //Google DNS: faster, supported by multiple data centers all around the world
    public static InetAddress GOOGLE_PRIMARY_DNS;
    public static InetAddress GOOGLE_SECONDARY_DNS;
    //Cloudflare DNS: internet’s fastest DNS directory
    public static InetAddress CLOUDFLARE_PRIMARY_DNS;
    public static InetAddress CLOUDFLARE_SECONDARY_DNS;

    private final DynamicHostnameGenerator hostnameGenerator;

    public BdxlLocator(Mode mode) {
        this(
                mode.getString("lookup.locator.bdxl.prefix"),
                mode.getString("lookup.locator.hostname"),
                mode.getString("lookup.locator.bdxl.algorithm"),
                EncodingUtils.get(mode.getString("lookup.locator.bdxl.encoding")),
                Long.parseLong(mode.getString("lookup.locator.bdxl.timeout")),
                Integer.parseInt(mode.getString("lookup.locator.bdxl.maxRetries")),
                Boolean.parseBoolean(mode.getString("lookup.locator.bdxl.enablePublicDNS"))
        );

        try {
            GOOGLE_PRIMARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (8 & 0xff), (byte) (8 & 0xff), (byte) (8 & 0xff), (byte) (8 & 0xff)}));
            GOOGLE_SECONDARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (8 & 0xff), (byte) (8 & 0xff), (byte) (4 & 0xff), (byte) (4 & 0xff)}));

            CLOUDFLARE_PRIMARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (1 & 0xff), (byte) (1 & 0xff), (byte) (1 & 0xff), (byte) (1 & 0xff)}));
            CLOUDFLARE_SECONDARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (1 & 0xff), (byte) (0), (byte) (0), (byte) (1 & 0xff)}));
        } catch (UnknownHostException e) {
            //Unable to initialize Custom DNS server
        }

        if (enablePublicDNS) {
            customDNSServers.add(GOOGLE_PRIMARY_DNS);
            customDNSServers.add(GOOGLE_SECONDARY_DNS);
            customDNSServers.add(CLOUDFLARE_PRIMARY_DNS);
            customDNSServers.add(CLOUDFLARE_SECONDARY_DNS);
        }
    }

    /**
     * Initiate a new instance of BDXL lookup functionality using SHA-256 for hashing.
     *
     * @param hostname Hostname used as base for lookup.
     */
    @SuppressWarnings("unused")
    public BdxlLocator(String hostname) {
        this("", hostname, "SHA-256", 30L, 3, false);
    }

    /**
     * Initiate a new instance of BDXL lookup functionality.
     *
     * @param hostname        Hostname used as base for lookup.
     * @param digestAlgorithm Algorithm used for generation of hostname.
     */
    public BdxlLocator(String hostname, String digestAlgorithm) {
        this("", hostname, digestAlgorithm, 30L, 3, false);
    }

    /**
     * Initiate a new instance of BDXL lookup functionality.
     *
     * @param prefix          Value attached in front of calculated hash.
     * @param hostname        Hostname used as base for lookup.
     * @param digestAlgorithm Algorithm used for generation of hostname.
     * @param timeout         Lookup timeout
     * @param maxRetries      Maximum number of retries
     * @param enablePublicDNS Enable custom DNS lookup
     */
    public BdxlLocator(String prefix, String hostname, String digestAlgorithm, long timeout, int maxRetries, boolean enablePublicDNS) {
        this(prefix, hostname, digestAlgorithm, BaseEncoding.base32(), timeout, maxRetries, enablePublicDNS);
    }

    /**
     * Initiate a new instance of BDXL lookup functionality.
     *
     * @param prefix          Value attached in front of calculated hash.
     * @param hostname        Hostname used as base for lookup.
     * @param digestAlgorithm Algorithm used for generation of hostname.
     * @param encoding        Encoding of hash for hostname.
     * @param timeout         Lookup timeout
     * @param maxRetries      Maximum number of retries
     * @param enablePublicDNS Enable custom DNS lookup
     */
    public BdxlLocator(String prefix, String hostname, String digestAlgorithm, BaseEncoding encoding, long timeout, int maxRetries, boolean enablePublicDNS) {
        this.timeout = timeout;
        this.maxRetries = maxRetries;
        this.enablePublicDNS = enablePublicDNS;
        hostnameGenerator = new DynamicHostnameGenerator(prefix, hostname, digestAlgorithm, encoding);
    }

    /**
     * Resolves a participant identifier to an SMP URI via NAPTR DNS lookup against the SML.
     * - Generates the lookup hostname from the participant identifier,
     * - queries DNS for NAPTR records,
     * - extracts the SMP endpoint URI from the matching record's regex field.
     *
     * @param participantIdentifier the participant to look up in SML
     * @return the SMP URI for the participant
     * @throws PeppolResourceException       if the participant is not registered in SML (HOST_NOT_FOUND / TYPE_NOT_FOUND)
     * @throws PeppolInfrastructureException if SML DNS is not responding or returned a server error (TRY_AGAIN / UNRECOVERABLE)
     * @throws LookupException               if an unexpected error occurred during lookup
     */
    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        // Create hostname for participant identifier.
        String hostname = hostnameGenerator.generate(participantIdentifier).replaceAll("=*", "");

        ExtendedResolver extendedResolver = getExtendedResolver(hostname);

        Record[] records = performLookupWithRetry(hostname, extendedResolver, participantIdentifier);

        return extractSmpUriFromRecords(records, hostname);
    }

    /**
     * Creates and configures a DNS resolver. When public DNS is enabled, uses Google and
     * Cloudflare servers for resilience against local DNS issues. Otherwise falls back to
     * the system's default resolver.
     */
    private ExtendedResolver getExtendedResolver(String hostname) {
        ExtendedResolver extendedResolver;
        if (enablePublicDNS) {
            return CustomExtendedDNSResolver.createExtendedResolver(customDNSServers, timeout, maxRetries);
        } else {
            extendedResolver = new ExtendedResolver();
            try {
                if (StringUtils.isNotBlank(hostname)) {
                    extendedResolver.addResolver(new SimpleResolver(hostname));
                }
            } catch (final UnknownHostException ex) {
                //Primary DNS lookup fail, now try with default resolver
            }
            extendedResolver.addResolver(Lookup.getDefaultResolver());
        }
        extendedResolver.setRetries(maxRetries);
        extendedResolver.setTimeout(Duration.ofSeconds(timeout));
        return extendedResolver;
    }


    /**
     * Performs NAPTR DNS lookup with retry logic.
     * Tries UDP first, then falls back to TCP if UDP retries are exhausted.
     *
     * @throws PeppolResourceException       If participant is definitively not registered in SML.
     * @throws PeppolInfrastructureException If SML DNS is not responding or returned a server error.
     */
    private Record[] performLookupWithRetry(String hostname, ExtendedResolver extendedResolver, ParticipantIdentifier participantIdentifier)
            throws LookupException {

        final Lookup naptrLookup = getLookup(hostname, extendedResolver);

        Record[] records = executeWithRetry(naptrLookup, maxRetries);

        // UDP exhausted retries with TRY_AGAIN, falling back to TCP
        if (naptrLookup.getResult() == Lookup.TRY_AGAIN) {
            log.debug("UDP DNS lookup exhausted retries for '{}', falling back to TCP", hostname);
            extendedResolver.setTCP(true);
            records = executeWithRetry(naptrLookup, maxRetries);
        }

        if (naptrLookup.getResult() == Lookup.SUCCESSFUL) {
            return records;
        }

        // Lookup failed — translate DNS result code to the appropriate domain-specific exception
        handleLookupFailure(naptrLookup, participantIdentifier);

        throw new LookupException("Unexpected state after DNS lookup for: " + hostname);
    }

    // factory method to create a Lookup object for NAPTR records
    private static Lookup getLookup(String hostname, ExtendedResolver extendedResolver) throws LookupException {
        try {
            Lookup naptrLookup = new Lookup(hostname, Type.NAPTR);
            naptrLookup.setResolver(extendedResolver);
            return naptrLookup;
        } catch (TextParseException e) {
            throw new LookupException("Error when creating DNS lookup for hostname: " + hostname, e);
        }
    }

    /**
     * Executes a DNS lookup with the specified number of retries.
     * Only retries on TRY_AGAIN results (transient DNS errors).
     */
    private Record[] executeWithRetry(Lookup lookup, int retries) {
        Record[] records;
        int retriesLeft = retries;
        do {
            records = lookup.run();
            --retriesLeft;
        } while (lookup.getResult() == Lookup.TRY_AGAIN && retriesLeft >= 0);
        return records;
    }

    /**
     * Extracts the SMP URI from NAPTR DNS records. Filters for records with
     * service {@code "Meta:SMP"} and flag {@code "U"}, then applies the record's
     * regex to derive the final SMP endpoint URI.
     *
     * @throws PeppolResourceException if no matching NAPTR record is found
     */
    private URI extractSmpUriFromRecords(Record[] records, String hostname) throws PeppolResourceException {
        if (records == null) {
            throw new PeppolResourceException("Record for SMP not found in SML.");
        }

        for (Record dnsRecord : records) {
            // Guard against unexpected record types in the DNS response
            if (!(dnsRecord instanceof NAPTRRecord)) {
                continue;
            }

            NAPTRRecord naptrRecord = (NAPTRRecord) dnsRecord;

            // Handle only those having "Meta:SMP" as service.
            if ("Meta:SMP".equals(naptrRecord.getService()) && "U".equalsIgnoreCase(naptrRecord.getFlags())) {

                // Create URI and return.
                String result = handleRegex(naptrRecord.getRegexp(), hostname);
                if (result != null)
                    return URI.create(result);
            }
        }

        throw new PeppolResourceException("Record for SMP not found in SML.");
    }


    /**
     * Translates DNS lookup result codes into domain-specific exception types.
     *
     * <ul>
     *   <li>HOST_NOT_FOUND, TYPE_NOT_FOUND → {@link PeppolResourceException}
     *       (definitive — participant not currently registered in SML.
     *        Retry after verifying the identifier or SMP registration details)</li>
     *   <li>TRY_AGAIN → {@link PeppolInfrastructureException}
     *       (transient — SML DNS not responding, retry may help)</li>
     *   <li>UNRECOVERABLE → {@link PeppolInfrastructureException}
     *       (SML DNS server error — retry unlikely to help immediately)</li>
     * </ul>
     */
    private void handleLookupFailure(Lookup naptrLookup,
                                     ParticipantIdentifier participantIdentifier) throws LookupException {
        String identifier = participantIdentifier.getIdentifier();

        switch (naptrLookup.getResult()) {
            case Lookup.HOST_NOT_FOUND:
                // Definitive: participant does not exist in SML
                throw new PeppolResourceException(String.format("Participant with identifier '%s' is not registered in SML. " +
                        "The host does not exist", identifier));

            case Lookup.TYPE_NOT_FOUND:
                // Definitive: host exists but no NAPTR records — not properly registered
                throw new PeppolResourceException(String.format("Participant with identifier '%s' is not registered in SML. " +
                        "The Host exists, but has no records associated with the queried type", identifier));

            case Lookup.TRY_AGAIN:
                // Transient: SML DNS infrastructure issue — retryable
                throw new PeppolInfrastructureException(String.format("SML lookup failed for identifier '%s' due to network error. " +
                        "Retry may help. DNS-Lookup-Err: %s", identifier, naptrLookup.getErrorString()));

            case Lookup.UNRECOVERABLE:
                // SML DNS server error — PEPPOL infrastructure fault
                throw new PeppolInfrastructureException(String.format("SML lookup failed for identifier '%s' due to a data or server error. " +
                        "Repeating the lookup immediately would not be helpful. DNS-Lookup-Err: %s", identifier, naptrLookup.getErrorString()));

            default:
                throw new LookupException(String.format("Error when looking up identifier '%s' in SML. DNS-Lookup-Err: %s",
                        identifier, naptrLookup.getErrorString()));
        }
    }

    public static String handleRegex(String naptrRegex, String hostname) {
        String[] regexp = naptrRegex.split("!");

        // Simple stupid
        if (".*".equals(regexp[1]))
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
