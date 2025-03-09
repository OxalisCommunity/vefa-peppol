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

import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.NotFoundException;
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

    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        // Create hostname for participant identifier.
        String hostname = hostnameGenerator.generate(participantIdentifier);

        ExtendedResolver extendedResolver;
        try {
            if(enablePublicDNS) {
                extendedResolver = CustomExtendedDNSResolver.createExtendedResolver(customDNSServers, timeout, maxRetries);
            } else {
                extendedResolver = new ExtendedResolver();
                try {
                    if (StringUtils.isNotBlank(hostname)) {
                        extendedResolver.addResolver(new SimpleResolver(hostname));
                    }
                } catch (final UnknownHostException ex) {
                    //Primary DNS lookup fail, now try with default resolver
                }
                extendedResolver.addResolver (Lookup.getDefaultResolver ());
            }
            extendedResolver.setRetries(maxRetries);
            extendedResolver.setTimeout(Duration.ofSeconds(timeout));

            final Lookup lookup = new Lookup(hostname);
            lookup.setResolver(extendedResolver);

            int retryCountLeft = maxRetries;
            // Retry, The lookup may fail due to a network error. Repeating the lookup might be helpful
            do {
                lookup.run();
                --retryCountLeft;
            } while (lookup.getResult() == Lookup.TRY_AGAIN && retryCountLeft >= 0);

            // Retry with TCP as well
            if (lookup.getResult() == Lookup.TRY_AGAIN) {
                extendedResolver.setTCP(true);

                retryCountLeft = maxRetries;
                do {
                    lookup.run();
                    --retryCountLeft;
                } while (lookup.getResult() == Lookup.TRY_AGAIN && retryCountLeft >= 0);
            }

            if (lookup.getResult() != Lookup.SUCCESSFUL) {
                switch (lookup.getResult()) {
                    case Lookup.HOST_NOT_FOUND: // The host does not exist
                        throw new NotFoundException(String.format("Identifier '%s' is not registered in SML. The host '%s' does not exist", participantIdentifier.getIdentifier(), hostname));
                    case Lookup.TYPE_NOT_FOUND: // The host exists, but has no records associated with the queried type
                        throw new NotFoundException(String.format("Identifier '%s' is not registered in SML. The Host '%s' exists, but has no records associated with the queried type", participantIdentifier.getIdentifier(), hostname));
                    case Lookup.TRY_AGAIN: // The lookup failed due to a network error. Repeating the lookup may be helpful.
                        throw new LookupException(String.format("Error when looking up identifier '%s' in SML due to network error. Retry after sometime... DNS-Lookup-Err: %s", participantIdentifier.getIdentifier(), lookup.getErrorString()));
                    case Lookup.UNRECOVERABLE: // The lookup failed due to a data or server error. Repeating the lookup would not be helpful.
                        throw new LookupException(String.format("Error when looking up identifier '%s' in SML due to a data or server error. Repeating the lookup immediately would not be helpful. DNS-Lookup-Err: %s", participantIdentifier.getIdentifier(), lookup.getErrorString()));
                    default:
                        throw new LookupException(String.format("Error when looking up identifier '%s' in SML. DNS-Lookup-Err: %s", participantIdentifier.getIdentifier(), lookup.getErrorString()));
                }
            }
        } catch (TextParseException e) {
            throw new LookupException(e.getMessage(), e);
        }

        return URI.create(String.format("http://%s", hostname));
    }
}
