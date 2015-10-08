package no.difi.vefa.peppol.security.xmldsig;

import no.difi.vefa.peppol.security.api.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import java.security.cert.X509Certificate;

public class XmldsigVerifier {

    private static Logger logger = LoggerFactory.getLogger(XmldsigVerifier.class);

    public static X509Certificate verify(Document document) throws SecurityException {
        try {
            NodeList nl = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0)
                throw new SecurityException("Cannot find Signature element");

            X509KeySelector keySelector = new X509KeySelector();
            DOMValidateContext valContext = new DOMValidateContext(keySelector, nl.item(0));

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);

            if (!signature.validate(valContext))
                throw new SecurityException("Signature failed.");

            logger.debug("Signature passed.");
            return keySelector.getCertificate();
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new SecurityException("Unable to verify document signature.", e);
        }
    }
}
