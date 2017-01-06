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

package no.difi.vefa.peppol.common.code;

public enum DigestMethod {

    SHA1("http://www.w3.org/2000/09/xmldsig#sha1"),
    SHA256("http://www.w3.org/2001/04/xmlenc#sha256"),
    SHA512("http://www.w3.org/2001/04/xmlenc#sha512");

    private final String uri;

    DigestMethod(String uri) {
        this.uri = uri;
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
