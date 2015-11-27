package no.difi.vefa.peppol.common.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DomUtils {

    private static DocumentBuilderFactory documentBuilderFactory;

    static {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
    }

    public static Document parse(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        return documentBuilderFactory.newDocumentBuilder().parse(inputStream);
    }

    public static byte[] convertToXml(Document document) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            ByteArrayOutputStream signedRemXmlByteArray = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(signedRemXmlByteArray);
            t.transform(source, result);
            return signedRemXmlByteArray.toByteArray();
        } catch (TransformerException e) {
            throw new IllegalStateException("Unable to transform DOM Document to XML representation");
        }

    }

}
