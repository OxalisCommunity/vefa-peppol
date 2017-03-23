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

package no.difi.vefa.peppol.publisher;

import no.difi.vefa.peppol.publisher.lang.SigningException;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigSigner;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.security.KeyStore;

/**
 * @author erlend
 */
public class Signer {

    private XmldsigSigner xmldsigSigner;

    private KeyStore.PrivateKeyEntry privateKeyEntry;

    public Signer(XmldsigSigner xmldsigSigner, KeyStore.PrivateKeyEntry privateKeyEntry) {
        this.xmldsigSigner = xmldsigSigner;
        this.privateKeyEntry = privateKeyEntry;
    }

    public void sign(Document document, OutputStream outputStream) throws SigningException {
        try {
            xmldsigSigner.sign(document, privateKeyEntry, new StreamResult(outputStream));
        } catch (PeppolSecurityException e) {
            throw new SigningException(e.getMessage(), e);
        }
    }
}
