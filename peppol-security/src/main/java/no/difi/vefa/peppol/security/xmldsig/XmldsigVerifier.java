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

import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import java.security.cert.X509Certificate;

public class XmldsigVerifier {

    public static X509Certificate verify(Document document) throws PeppolSecurityException {
        try {
            NodeList nl = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0)
                throw new PeppolSecurityException("Cannot find Signature element");

            X509KeySelector keySelector = new X509KeySelector();
            DOMValidateContext valContext = new DOMValidateContext(keySelector, nl.item(0));

            XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
            XMLSignature signature = xmlSignatureFactory.unmarshalXMLSignature(valContext);

            if (!signature.validate(valContext))
                throw new PeppolSecurityException("Signature failed.");

            return keySelector.getCertificate();
        } catch (XMLSignatureException | MarshalException e) {
            throw new PeppolSecurityException("Unable to verify document signature.", e);
        }
    }

    XmldsigVerifier() {

    }
}
