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

package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.common.model.Signed;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

public class SignedEvidenceReader {

    public static Signed<Evidence> read(InputStream inputStream) throws RemEvidenceException, PeppolSecurityException {
        try {
            return read(RemHelper.DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(inputStream));
        } catch (SAXException | ParserConfigurationException | IOException e) {
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
