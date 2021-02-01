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
import org.xbill.DNS.Lookup;
import org.xbill.DNS.TextParseException;

import java.net.URI;

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
            if (lookup.run() == null) {
                if(lookup.getResult() == Lookup.HOST_NOT_FOUND) {
                    throw new NotFoundException(
                            String.format("Identifier '%s' is not registered in SML.", participantIdentifier.getIdentifier()));
                } else {
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
