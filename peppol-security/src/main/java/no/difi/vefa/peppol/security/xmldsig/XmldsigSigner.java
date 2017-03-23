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

import no.difi.vefa.peppol.common.api.PerformAction;
import no.difi.vefa.peppol.common.lang.PeppolRuntimeException;
import no.difi.vefa.peppol.common.util.ExceptionUtil;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmldsigSigner {

    private static TransformerFactory transformerFactory;

    private static XMLSignatureFactory xmlSignatureFactory;

    private String digestMethod;

    private String signatureMethod;

    static {
        ExceptionUtil.perform(PeppolRuntimeException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                transformerFactory = TransformerFactory.newInstance();

                xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
            }
        });
    }

    public static XmldsigSigner SHA1() {
        return new XmldsigSigner(DigestMethod.SHA1, SignatureMethod.RSA_SHA1);
    }

    public static XmldsigSigner SHA256() {
        return new XmldsigSigner(DigestMethod.SHA256, ExtraSignatureMethod.RSA_SHA256);
    }

    XmldsigSigner(String digestMethod, String signatureMethod) {
        this.digestMethod = digestMethod;
        this.signatureMethod = signatureMethod;
    }

    public void sign(Document document, KeyStore.PrivateKeyEntry privateKeyEntry, Result result)
            throws PeppolSecurityException {
        sign(document.getDocumentElement(), privateKeyEntry, result);
    }

    public void sign(Element element, KeyStore.PrivateKeyEntry privateKeyEntry, Result result)
            throws PeppolSecurityException {
        try {
            Reference reference = xmlSignatureFactory.newReference(
                    "",
                    xmlSignatureFactory.newDigestMethod(digestMethod, null),
                    Collections.singletonList(
                            xmlSignatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)
                    ),
                    null, null
            );

            SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(
                    xmlSignatureFactory.newCanonicalizationMethod(
                            CanonicalizationMethod.INCLUSIVE,
                            (C14NMethodParameterSpec) null
                    ),
                    xmlSignatureFactory.newSignatureMethod(signatureMethod, null),
                    Collections.singletonList(reference)
            );

            X509Certificate certificate = (X509Certificate) privateKeyEntry.getCertificate();
            List<Object> aX509Content = new ArrayList<>();
            aX509Content.add(certificate.getSubjectX500Principal().getName());
            aX509Content.add(certificate);

            KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
            X509Data x509Data = keyInfoFactory.newX509Data(aX509Content);
            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

            XMLSignature signature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo);

            DOMSignContext domSignContext = new DOMSignContext(privateKeyEntry.getPrivateKey(), element);

            // Sign document
            signature.sign(domSignContext);

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setParameter(OutputKeys.INDENT, "no");
            transformer.transform(new DOMSource(element), result);
        } catch (Exception e) {
            throw new PeppolSecurityException(e.getMessage(), e);
        }
    }
}
