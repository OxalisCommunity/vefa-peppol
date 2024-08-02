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
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.NotFoundException;
import network.oxalis.vefa.peppol.lookup.util.DynamicHostnameGenerator;
import network.oxalis.vefa.peppol.lookup.util.EncodingUtils;
import network.oxalis.vefa.peppol.mode.Mode;
import org.xbill.DNS.*;

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
 */
public class BdxlLocator extends AbstractLocator {

    private long timeout = 20L;
    private int maxRetries = 3;

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
                EncodingUtils.get(mode.getString("lookup.locator.bdxl.encoding"))
        );
        maxRetries = Integer.parseInt(mode.getString("lookup.locator.bdxl.maxRetries"));
        timeout = Long.parseLong(mode.getString("lookup.locator.bdxl.timeout"));

        try {
            GOOGLE_PRIMARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (8 & 0xff), (byte) (8 & 0xff), (byte) (8 & 0xff), (byte) (8 & 0xff)}));
            GOOGLE_SECONDARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (8 & 0xff), (byte) (8 & 0xff), (byte) (4 & 0xff), (byte) (4 & 0xff)}));

            CLOUDFLARE_PRIMARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (1 & 0xff), (byte) (1 & 0xff), (byte) (1 & 0xff), (byte) (1 & 0xff)}));
            CLOUDFLARE_SECONDARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (1 & 0xff), (byte) (0 & 0xff), (byte) (0 & 0xff), (byte) (1 & 0xff)}));
        } catch (UnknownHostException e) {
            //Unable to initialize Custom DNS server Primary DNS lookup fail, now try with default resolver
        }

        customDNSServers.add(GOOGLE_PRIMARY_DNS);
        customDNSServers.add(GOOGLE_SECONDARY_DNS);
        customDNSServers.add(CLOUDFLARE_PRIMARY_DNS);
        customDNSServers.add(CLOUDFLARE_SECONDARY_DNS);
    }

    /**
     * Initiate a new instance of BDXL lookup functionality using SHA-256 for hashing.
     *
     * @param hostname Hostname used as base for lookup.
     */
    @SuppressWarnings("unused")
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
            ExtendedResolver extendedResolver = CustomExtendedDNSResolver.createExtendedResolver(customDNSServers, timeout, maxRetries);
            extendedResolver.setRetries(maxRetries);
            extendedResolver.setTimeout(Duration.ofSeconds(timeout));

            // Fetch all records of type NAPTR registered on hostname.
            final Lookup naptrLookup = new Lookup(hostname, Type.NAPTR);
            naptrLookup.setResolver(extendedResolver);

            Record[] records;
            int retryCountLeft = maxRetries;
            // Retry, the NAPTR lookup may fail due to a network error. Repeating the lookup might be helpful
            do {
                records = naptrLookup.run();
                --retryCountLeft;
            } while (naptrLookup.getResult() == Lookup.TRY_AGAIN && retryCountLeft >= 0);

            // Retry with TCP as well
            if (naptrLookup.getResult() == Lookup.TRY_AGAIN) {
                extendedResolver.setTCP(true);

                retryCountLeft = maxRetries;
                do {
                    records = naptrLookup.run();
                    --retryCountLeft;
                } while (naptrLookup.getResult() == Lookup.TRY_AGAIN && retryCountLeft >= 0);
            }

            if (naptrLookup.getResult() != Lookup.SUCCESSFUL) {
                switch (naptrLookup.getResult()) {
                    case Lookup.HOST_NOT_FOUND: // HOST_NOT_FOUND = The host does not exist
                    case Lookup.TYPE_NOT_FOUND: // TYPE_NOT_FOUND = The host exists, but has no records associated with the queried type
                        throw new NotFoundException(String.format("Identifier '%s' is not registered in SML.", participantIdentifier.getIdentifier()));
                    case Lookup.TRY_AGAIN: // Since we already tried a couple of times with TRY_AGAIN for TCP and UDP, now giving up ...
                    default:
                        throw new LookupException(String.format("Error when looking up identifier '%s' in SML. DNS-Lookup-Err: %s", participantIdentifier.getIdentifier(), naptrLookup.getErrorString()));
                }
            }

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

        throw new NotFoundException("Record for SMP not found in SML.");
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
