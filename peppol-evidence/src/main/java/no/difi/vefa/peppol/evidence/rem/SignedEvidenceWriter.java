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

package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigSigner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.security.KeyStore;

public class SignedEvidenceWriter {

    public static void write(OutputStream outputStream, KeyStore.PrivateKeyEntry privateKeyEntry, Evidence evidence)
            throws RemEvidenceException, PeppolSecurityException {
        write(privateKeyEntry, evidence, new StreamResult(outputStream));
    }

    public static Document write(KeyStore.PrivateKeyEntry privateKeyEntry, Evidence evidence)
            throws RemEvidenceException, PeppolSecurityException {
        Document document = RemHelper.getDocumentBuilder().newDocument();
        write(document, privateKeyEntry, evidence);
        return document;
    }

    public static void write(Node node, KeyStore.PrivateKeyEntry privateKeyEntry, Evidence evidence)
            throws RemEvidenceException, PeppolSecurityException {
        write(privateKeyEntry, evidence, new DOMResult(node));
    }

    public static void write(final KeyStore.PrivateKeyEntry privateKeyEntry,
                             final Evidence evidence, final Result result)
            throws RemEvidenceException, PeppolSecurityException {
        Document document = RemHelper.getDocumentBuilder().newDocument();
        EvidenceWriter.write(document, evidence);

        XmldsigSigner.SHA256().sign(document, privateKeyEntry, result);
    }
}
