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

import no.difi.vefa.peppol.common.model.Signed;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

public class SignedEvidenceReader {

    public static Signed<Evidence> read(InputStream inputStream) throws RemEvidenceException, PeppolSecurityException {
        try {
            return read(RemHelper.getDocumentBuilder().parse(inputStream));
        } catch (SAXException | IOException e) {
            throw new RemEvidenceException(e.getMessage(), e);
        }
    }

    public static Signed<Evidence> read(Node node) throws RemEvidenceException, PeppolSecurityException {
        if (!(node instanceof Document))
            throw new RemEvidenceException("Node of type Document required.");

        X509Certificate certificate = XmldsigVerifier.verify((Document) node);
        return Signed.of(EvidenceReader.read(node), certificate);
    }
}
