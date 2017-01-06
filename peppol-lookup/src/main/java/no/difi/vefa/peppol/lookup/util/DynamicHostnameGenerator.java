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

    private BaseEncoding encoding;

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
        this(prefix, hostname, digestAlgorithm, BaseEncoding.base16());
    }

    public DynamicHostnameGenerator(String prefix, String hostname, String digestAlgorithm, BaseEncoding encoding) {
        this.prefix = prefix;
        this.hostname = hostname;
        this.digestAlgorithm = digestAlgorithm;
        this.encoding = encoding;
    }

    public String generate(ParticipantIdentifier participantIdentifier) throws LookupException {
        String receiverHash;

        try {
            // Create digest based on participant identifier.
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
            byte[] digest = md.digest(participantIdentifier.getIdentifier().getBytes(StandardCharsets.UTF_8));

            // Create hex of digest.
            receiverHash = encoding.encode(digest).toLowerCase();
        } catch (Exception e) {
            throw new LookupException(e.getMessage(), e);
        }

        return String.format("%s%s.%s.%s",
                prefix, receiverHash, participantIdentifier.getScheme().getValue(), hostname);
    }
}
