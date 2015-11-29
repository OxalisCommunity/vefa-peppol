package no.difi.vefa.peppol.evidence.rem;

import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Transforms SignedRemEvidence back and forth between various representations like for instance
 * W3C Document and XML.
 *
 * <p>
 * The constructor is package protected as you are expected to use the {@link RemEvidenceService}  to
 * create instances of this class.
 * <p>
 * Created by steinar on 08.11.2015.
 */
class RemEvidenceTransformer {


    private JAXBContext jaxbContext;
    private boolean formattedOutput = true;

    public RemEvidenceTransformer(JAXBContext jaxbContext) {

        this.jaxbContext = jaxbContext;
    }

    /**
     * Transforms the supplied signed REM Evidence into it's XML representation.
     * <p>
     * NOTE! Do not use this XML representation for signature validation as
     *
     * @param signedRemEvidence
     * @param outputStream
     */
    public void formattedXml(SignedRemEvidence signedRemEvidence, OutputStream outputStream) {
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("Unable to crate a new transformer");
        }

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StreamResult result = new StreamResult(outputStream);
        DOMSource source = new DOMSource(signedRemEvidence.getDocument());
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IllegalStateException("Transformation of SignedRemEvidence to XML failed:" + e.getMessage(), e);
        }
    }

    /**
     * Parses the contents of an InputStream, which is expected to supply
     * a signed REMEvidenceType in XML representation.
     *
     * Step 1: parses xml into W3C Document
     * Step 2: converts W3C Document into JAXBElement
     *
     * @param inputStream holding the xml representation of a signed REM evidence.
     * @return
     */
    public SignedRemEvidence parse(InputStream inputStream) {

        Document parsedDocument;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            parsedDocument = documentBuilder.parse(inputStream);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Unable to create DocumentBuilder " + e.getMessage(), e);
        } catch (SAXException | IOException e) {
            throw new IllegalStateException("Unable to parse xml input " + e.getMessage(), e);
        }

        JAXBElement<REMEvidenceType> remEvidenceTypeJAXBElement = RemEvidenceBuilder.convertRemFromDocumentToJaxb(parsedDocument, jaxbContext);

        return new SignedRemEvidence(remEvidenceTypeJAXBElement, parsedDocument);
    }

    public boolean isFormattedOutput() {
        return formattedOutput;
    }

    public void setFormattedOutput(boolean formattedOutput) {
        this.formattedOutput = formattedOutput;
    }
}
