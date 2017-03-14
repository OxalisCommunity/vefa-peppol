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
