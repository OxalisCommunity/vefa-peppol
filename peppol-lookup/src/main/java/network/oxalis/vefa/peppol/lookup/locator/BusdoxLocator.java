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

import java.net.URI;
import java.net.UnknownHostException;
import java.time.Duration;

public class BusdoxLocator extends AbstractLocator {

    private DynamicHostnameGenerator hostnameGenerator;

    public BusdoxLocator(Mode mode) {
        this(
                mode.getString("lookup.locator.busdox.prefix"),
                mode.getString("lookup.locator.hostname"),
                mode.getString("lookup.locator.busdox.algorithm")
        );
    }

    @SuppressWarnings("unused")
    public BusdoxLocator(String hostname) {
        this("B-", hostname, "MD5");
    }

    public BusdoxLocator(String prefix, String hostname, String algorithm) {
        hostnameGenerator = new DynamicHostnameGenerator(prefix, hostname, algorithm);
    }

    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        // Create hostname for participant identifier.
        String hostname = hostnameGenerator.generate(participantIdentifier);

        try {
            final Lookup lookup = new Lookup(hostname);
            final int retries = 3;

            ExtendedResolver  extendedResolver = new ExtendedResolver();
            try {
                if (StringUtils.isNotBlank(hostname)) {
                    extendedResolver.addResolver(new SimpleResolver(hostname));
                }
            } catch (final UnknownHostException ex) {
                //Primary DNS lookup fail, now try with default resolver
            }
            extendedResolver.addResolver (Lookup.getDefaultResolver ());
            extendedResolver.setTimeout (Duration.ofSeconds (30L));
            extendedResolver.setRetries (retries);
            lookup.setResolver (extendedResolver);

            int retryCountLeft = retries;
            // TRY_AGAIN = The lookup failed due to a network error. Repeating the lookup may be helpful
            do {
                lookup.run();
                --retryCountLeft;
            } while (lookup.getResult () == Lookup.TRY_AGAIN && retryCountLeft >= 0);

            // Retry with TCP as well
            if (lookup.getResult () == Lookup.TRY_AGAIN) {
                extendedResolver.setTCP (true);

                retryCountLeft = retries;
                do {
                    lookup.run();
                    --retryCountLeft;
                } while (lookup.getResult () == Lookup.TRY_AGAIN && retryCountLeft >= 0);
            }

            if (lookup.getResult () != Lookup.SUCCESSFUL) {
                // HOST_NOT_FOUND = The host does not exist
                // TYPE_NOT_FOUND = The host exists, but has no records associated with the queried type
                // Since we already tried couple of times with TRY_AGAIN for TCP and UDP, now giving up ...
                if(lookup.getResult() == Lookup.HOST_NOT_FOUND || lookup.getResult() == Lookup.TRY_AGAIN
                        || lookup.getResult() == Lookup.TYPE_NOT_FOUND) {
                    throw new NotFoundException(
                            String.format("Identifier '%s' is not registered in SML.", participantIdentifier.getIdentifier()));
                } else {
                    // Attribute to UNRECOVERABLE error, repeating the lookup would not be helpful
                    throw new LookupException(
                            String.format("Error when looking up identifier '%s' in SML.", participantIdentifier.getIdentifier()));
                }
            }
        } catch (TextParseException e) {
            throw new LookupException(e.getMessage(), e);
        }

        return URI.create(String.format("http://%s", hostname));
    }
}
