package no.difi.vefa.peppol.security.xmldsig;

import com.google.common.io.ByteStreams;
import no.difi.vefa.peppol.common.util.DomUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class XmldsigTest {

    private KeyStore.PrivateKeyEntry privateKeyEntry;

    @BeforeClass
    public void setUp() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(getClass().getResourceAsStream("/keystore-self-signed.jks"), "changeit".toCharArray());

        privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("self-signed", new KeyStore.PasswordProtection("changeit".toCharArray()));
    }

    @Test
    public void simple() throws Exception {
        ByteArrayOutputStream generatedStream = new ByteArrayOutputStream();
        XmldsigSigner.SHA1().sign(DomUtils.parse(getClass().getResourceAsStream("/xmldsig-test-input.xml")), privateKeyEntry, new StreamResult(generatedStream));

        ByteArrayOutputStream expectedStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/xmldsig-test-output.xml"), expectedStream);

        Assert.assertEquals(generatedStream.toByteArray(), expectedStream.toByteArray());

        X509Certificate x509Certificate = XmldsigVerifier.verify(DomUtils.parse(new ByteArrayInputStream(generatedStream.toByteArray())));

        Assert.assertEquals(x509Certificate.getSubjectX500Principal().getName(), "CN=VEFA Validator self-signed,OU=Unknown,O=Unknown,L=Unknown,ST=Unknown,C=Unknown");
    }

    @Test
    public void simpleSHA256() throws Exception {
        ByteArrayOutputStream generatedStream = new ByteArrayOutputStream();
        XmldsigSigner.SHA256().sign(DomUtils.parse(getClass().getResourceAsStream("/xmldsig-test-input.xml")), privateKeyEntry, new StreamResult(generatedStream));

        ByteArrayOutputStream expectedStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/xmldsig-test-output-sha256.xml"), expectedStream);

        Assert.assertEquals(generatedStream.toByteArray(), expectedStream.toByteArray());

        X509Certificate x509Certificate = XmldsigVerifier.verify(DomUtils.parse(new ByteArrayInputStream(generatedStream.toByteArray())));

        Assert.assertEquals(x509Certificate.getSubjectX500Principal().getName(), "CN=VEFA Validator self-signed,OU=Unknown,O=Unknown,L=Unknown,ST=Unknown,C=Unknown");
    }

    @Test
    public void simpleContructors() {
        new XmldsigVerifier();
    }

    /**
     * Verifies that a simple class annotated with JAXB can be signed and validated.
     *
     * @throws Exception
     */
    @Test
    public void signAndValidate() throws Exception {

        Sample sample = new Sample();
        sample.setInfo("The quick brown fox");

        JAXBContext jaxbContext = JAXBContext.newInstance(Sample.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        DOMResult domResult = new DOMResult();

        marshaller.marshal(sample, domResult);

        Document node = (Document) domResult.getNode();
        Element documentElement = node.getDocumentElement();
        DOMResult signedResult = new DOMResult();
        XmldsigSigner.SHA1().sign(documentElement, privateKeyEntry, signedResult);

        // Verify the signature from the signed DOM document
        Document signedDocument = (Document) signedResult.getNode();
        X509Certificate verify = XmldsigVerifier.verify(signedDocument);
        Assert.assertNotNull(verify);
/*
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource source = new DOMSource(domResult.getNode());
        StreamResult result = new StreamResult(System.out);
        t.transform(source, result);
*/

    }
}
