/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.util.DynamicHostnameGenerator;
import no.difi.vefa.peppol.mode.Mode;
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
            if (new Lookup(hostname).run() == null)
                throw new LookupException(
                        String.format("Identifier '%s' not registered in SML.", participantIdentifier.getIdentifier()));
        } catch (TextParseException e) {
            throw new LookupException(e.getMessage(), e);
        }

        return URI.create(String.format("http://%s", hostname));
    }
}
