package no.difi.vefa.peppol.security.xmldsig;

import no.difi.vefa.peppol.common.util.DomUtils;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

public class XmldsigTest {

    @Test
    public void simple() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(getClass().getResourceAsStream("/keystore-self-signed.jks"), "changeit".toCharArray());

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("self-signed", new KeyStore.PasswordProtection("changeit".toCharArray()));

        ByteArrayOutputStream generatedStream = new ByteArrayOutputStream();
        XmldsigSigner.sign(DomUtils.parse(getClass().getResourceAsStream("/xmldsig-test-input.xml")), privateKeyEntry, new StreamResult(generatedStream));

        ByteArrayOutputStream expectedStream = new ByteArrayOutputStream();
        IOUtils.copy(getClass().getResourceAsStream("/xmldsig-test-output.xml"), expectedStream);

        Assert.assertEquals(generatedStream.toByteArray(), expectedStream.toByteArray());

        X509Certificate x509Certificate = XmldsigVerifier.verify(DomUtils.parse(new ByteArrayInputStream(generatedStream.toByteArray())));

        Assert.assertEquals(x509Certificate.getSubjectX500Principal().getName(), "CN=VEFA Validator self-signed,OU=Unknown,O=Unknown,L=Unknown,ST=Unknown,C=Unknown");
    }

    @Test
    public void simpleContructors() {
        new XmldsigSigner();
        new XmldsigVerifier();
    }
}
