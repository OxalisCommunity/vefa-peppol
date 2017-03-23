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

package no.difi.vefa.peppol.security.xmldsig;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;

class X509KeySelector extends KeySelector {
    private X509Certificate certificate;

    public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method,
                                    XMLCryptoContext context) throws KeySelectorException {
        Iterator ki = keyInfo.getContent().iterator();
        while (ki.hasNext()) {
            XMLStructure info = (XMLStructure) ki.next();
            if (!(info instanceof X509Data))
                continue;

            X509Data x509Data = (X509Data) info;
            Iterator xi = x509Data.getContent().iterator();
            while (xi.hasNext()) {
                Object o = xi.next();
                if (!(o instanceof X509Certificate))
                    continue;

                this.certificate = (X509Certificate) o;
                final PublicKey key = certificate.getPublicKey();

                // Make sure the algorithm is compatible with the method.
                if (algEquals(method.getAlgorithm(), key.getAlgorithm()))
                    return new KeySelectorResult() {
                        public Key getKey() {
                            return key;
                        }
                    };
            }
        }
        throw new KeySelectorException("No key found!");
    }

    private boolean algEquals(String algURI, String algName) {
        return ((algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) ||
                (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) ||
                (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(ExtraSignatureMethod.RSA_SHA256)) ||
                (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(ExtraSignatureMethod.RSA_SHA512)));
    }

    public X509Certificate getCertificate() {
        return certificate;
    }
}
