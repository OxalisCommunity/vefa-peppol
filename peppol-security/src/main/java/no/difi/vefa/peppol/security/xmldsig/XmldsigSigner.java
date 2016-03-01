package no.difi.vefa.peppol.security.xmldsig;

import no.difi.vefa.peppol.security.api.PeppolSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
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

    @SuppressWarnings("all")
    private static DocumentBuilderFactory documentBuilderFactory;
    private static TransformerFactory transformerFactory;
    private static XMLSignatureFactory xmlSignatureFactory;

    static {
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setCoalescing(true);
            documentBuilderFactory.setIgnoringComments(true);
            documentBuilderFactory.setNamespaceAware(true);

            transformerFactory = TransformerFactory.newInstance();

            xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static XmldsigSigner SHA1() {
        return new XmldsigSigner(DigestMethod.SHA1, SignatureMethod.RSA_SHA1);
    }

    public static XmldsigSigner SHA256() {
        return new XmldsigSigner(DigestMethod.SHA256, ExtraSignatureMethod.RSA_SHA256);
    }

    private String digestMethod;
    private String signatureMethod;

    XmldsigSigner(String digestMethod, String signatureMethod) {
        this.digestMethod = digestMethod;
        this.signatureMethod = signatureMethod;
    }

    public void sign(Document document, KeyStore.PrivateKeyEntry privateKeyEntry, Result result) throws PeppolSecurityException {
        sign(document.getDocumentElement(), privateKeyEntry, result);
    }

    public void sign(Element element, KeyStore.PrivateKeyEntry privateKeyEntry, Result result) throws PeppolSecurityException {
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
