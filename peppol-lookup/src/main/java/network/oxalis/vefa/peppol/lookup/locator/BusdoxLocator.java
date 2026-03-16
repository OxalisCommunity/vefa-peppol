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

import lombok.extern.slf4j.Slf4j;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.PeppolInfrastructureException;
import network.oxalis.vefa.peppol.lookup.api.PeppolResourceException;
import network.oxalis.vefa.peppol.lookup.util.DynamicHostnameGenerator;
import network.oxalis.vefa.peppol.mode.Mode;
import org.apache.commons.lang3.StringUtils;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Busdox SML locator — resolves a participant identifier to an SMP URI using A-record DNS lookups with MD5-based hostname generation.
 *
 * <p>DNS result codes are mapped to domain-specific exceptions at the SML/DNS layer,
 * these codes carry definitive meaning:</p>
 * <ul>
 *   <li>HOST_NOT_FOUND / TYPE_NOT_FOUND → {@link PeppolResourceException}
 *       — participant definitively not in SML</li>
 *   <li>TRY_AGAIN → {@link PeppolInfrastructureException}
 *       — transient DNS failure, retry may help</li>
 *   <li>UNRECOVERABLE → {@link PeppolInfrastructureException}
 *       — SML DNS server error</li>
 * </ul>
 *
 * @see BdxlLocator
 * @see PeppolResourceException
 * @see PeppolInfrastructureException
 */
@Slf4j
public class BusdoxLocator extends AbstractLocator {

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

    public BusdoxLocator(Mode mode) {
        this(
                mode.getString("lookup.locator.busdox.prefix"),
                mode.getString("lookup.locator.hostname"),
                mode.getString("lookup.locator.busdox.algorithm"),
                Long.parseLong(mode.getString("lookup.locator.busdox.timeout")),
                Integer.parseInt(mode.getString("lookup.locator.busdox.maxRetries")),
                Boolean.parseBoolean(mode.getString("lookup.locator.busdox.enablePublicDNS"))
        );

        try {
            GOOGLE_PRIMARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (8 & 0xff), (byte) (8 & 0xff), (byte) (8 & 0xff), (byte) (8 & 0xff)}));
            GOOGLE_SECONDARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (8 & 0xff), (byte) (8 & 0xff), (byte) (4 & 0xff), (byte) (4 & 0xff)}));

            CLOUDFLARE_PRIMARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (1 & 0xff), (byte) (1 & 0xff), (byte) (1 & 0xff), (byte) (1 & 0xff)}));
            CLOUDFLARE_SECONDARY_DNS = InetAddress.getByAddress((new byte[]{(byte) (1 & 0xff), (byte) (0 & 0xff), (byte) (0 & 0xff), (byte) (1 & 0xff)}));
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

    @SuppressWarnings("unused")
    public BusdoxLocator(String hostname) {
        this("B-", hostname, "MD5", 30L, 3, false);
    }

    public BusdoxLocator(String prefix, String hostname, String algorithm, long timeout, int maxRetries, boolean enablePublicDNS) {
        this.timeout = timeout;
        this.maxRetries = maxRetries;
        this.enablePublicDNS = enablePublicDNS;
        hostnameGenerator = new DynamicHostnameGenerator(prefix, hostname, algorithm);
    }

    /**
     * Resolves a participant identifier to an SMP URI via A-record DNS lookup against the SML.
     * - Generates the lookup hostname from the participant identifier using MD5 hashing,
     * - verifies the hostname resolves, and returns the SMP base URI.
     *
     * @param participantIdentifier the participant to look up in SML
     * @return the SMP base URI for the participant (http://{generated-hostname})
     * @throws PeppolResourceException       if the participant is not registered in SML (HOST_NOT_FOUND / TYPE_NOT_FOUND)
     * @throws PeppolInfrastructureException if SML DNS is not responding or returned a server error (TRY_AGAIN / UNRECOVERABLE)
     * @throws LookupException               if an unexpected error occurred during lookup
     */
    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        // Create hostname for participant identifier.
        String hostname = hostnameGenerator.generate(participantIdentifier);

        ExtendedResolver extendedResolver = getExtendedResolver(hostname);

        performLookupWithRetry(hostname, extendedResolver, participantIdentifier);

        return URI.create(String.format("http://%s", hostname));
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
     * Performs DNS A-record lookup with retry logic.
     * Tries UDP first, then falls back to TCP if UDP retries are exhausted.
     *
     * @throws PeppolResourceException       If participant is definitively not registered in SML.
     * @throws PeppolInfrastructureException If SML DNS is not responding or returned a server error.
     */
    private void performLookupWithRetry(String hostname, ExtendedResolver extendedResolver, ParticipantIdentifier participantIdentifier)
            throws LookupException {

        final Lookup lookup = getLookup(hostname, extendedResolver);

        executeWithRetry(lookup, maxRetries);

        // UDP exhausted retries with TRY_AGAIN, falling back to TCP
        if (lookup.getResult() == Lookup.TRY_AGAIN) {
            log.debug("UDP DNS lookup exhausted retries for '{}', falling back to TCP", hostname);
            extendedResolver.setTCP(true);
            executeWithRetry(lookup, maxRetries);
        }

        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            return;
        }

        // Lookup failed — translate DNS result code to the appropriate domain-specific exception
        handleLookupFailure(lookup, participantIdentifier);

        throw new LookupException("Unexpected state after DNS lookup for: " + hostname);
    }

    // factory method to create a Lookup object for NAPTR records
    private static Lookup getLookup(String hostname, ExtendedResolver extendedResolver) throws LookupException {
        try {
            Lookup lookup = new Lookup(hostname);
            lookup.setResolver(extendedResolver);
            return lookup;
        } catch (TextParseException e) {
            throw new LookupException("Error when creating DNS lookup for hostname: " + hostname, e);
        }
    }

    /**
     * Executes a DNS lookup with the specified number of retries.
     * Only retries on TRY_AGAIN results (transient DNS errors).
     */
    private void executeWithRetry(Lookup lookup, int retries) {
        int retriesLeft = retries;
        do {
            lookup.run();
            --retriesLeft;
        } while (lookup.getResult() == Lookup.TRY_AGAIN && retriesLeft >= 0);
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
    private void handleLookupFailure(Lookup lookup,
                                     ParticipantIdentifier participantIdentifier) throws LookupException {
        String identifier = participantIdentifier.getIdentifier();

        switch (lookup.getResult()) {
            case Lookup.HOST_NOT_FOUND:
                throw new PeppolResourceException(String.format("Participant with identifier '%s' is not registered in SML. " +
                        "The host does not exist", identifier));

            case Lookup.TYPE_NOT_FOUND:
                throw new PeppolResourceException(String.format("Participant with identifier '%s' is not registered in SML. " +
                        "The Host exists, but has no records associated with the queried type", identifier));

            case Lookup.TRY_AGAIN:
                throw new PeppolInfrastructureException(String.format("SML lookup failed for identifier '%s' due to network error. " +
                        "Retry may help. DNS-Lookup-Err: %s", identifier, lookup.getErrorString()));

            case Lookup.UNRECOVERABLE:
                throw new PeppolInfrastructureException(String.format("SML lookup failed for identifier '%s' due to a data or server error. " +
                        "Repeating the lookup immediately would not be helpful. DNS-Lookup-Err: %s", identifier, lookup.getErrorString()));

            default:
                throw new LookupException(String.format("Error when looking up identifier '%s' in SML. DNS-Lookup-Err: %s",
                        identifier, lookup.getErrorString()));
        }
    }

}
