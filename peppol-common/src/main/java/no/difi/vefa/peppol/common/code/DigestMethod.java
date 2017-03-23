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

package no.difi.vefa.peppol.common.code;

public enum DigestMethod {

    SHA1("SHA-1", "http://www.w3.org/2000/09/xmldsig#sha1"),
    SHA256("SHA-256", "http://www.w3.org/2001/04/xmlenc#sha256"),
    SHA512("SHA-512", "http://www.w3.org/2001/04/xmlenc#sha512");

    private final String identifier;

    private final String uri;

    DigestMethod(String identifier, String uri) {
        this.identifier = identifier;
        this.uri = uri;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUri() {
        return uri;
    }

    public static DigestMethod fromUri(String uri) {
        for (DigestMethod digestMethod : values())
            if (digestMethod.uri.equals(uri))
                return digestMethod;

        return null;
    }
}
