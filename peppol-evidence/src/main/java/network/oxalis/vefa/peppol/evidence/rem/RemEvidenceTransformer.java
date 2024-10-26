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

package network.oxalis.vefa.peppol.evidence.rem;

import network.oxalis.vefa.peppol.evidence.lang.RemEvidenceException;
import network.oxalis.vefa.peppol.evidence.jaxb.rem.REMEvidenceType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
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
 * <p/>
 * <p/>
 * The constructor is package protected as you are expected to use the {@link RemEvidenceService}  to
 * create instances of this class.
 * <p/>
 * Created by steinar on 08.11.2015.
 */
public class RemEvidenceTransformer {

    private boolean formattedOutput = true;

    /**
     * Transforms SignedRemEvidence into XML representation suitable for signature verification etc.
     * I.e. the output is not formatted.
     *
     * @param signedRemEvidence instance to be formatted.
     * @param outputStream
     * @throws RemEvidenceException
     */
    public void toUnformattedXml(SignedRemEvidence signedRemEvidence, OutputStream outputStream)
            throws RemEvidenceException {
        format(signedRemEvidence, outputStream, false);
    }

    /**
     * Transforms the supplied signed REM Evidence into it's formatted XML representation.
     * <p/>
     * NOTE! Do not use this XML representation for signature validation as this will fail.
     *
     * @param signedRemEvidence
     * @param outputStream
     * @throws RemEvidenceException
     */
    public void toFormattedXml(SignedRemEvidence signedRemEvidence, OutputStream outputStream)
            throws RemEvidenceException {
        format(signedRemEvidence, outputStream, true);
    }

    /**
     * Internal convenience method
     *
     * @param signedRemEvidence rem evidence to transform
     * @param outputStream      into which the formatted output should be emitted.
     * @param formatted         indicates whether the output should be formatted (true) or not (false)
     * @throws RemEvidenceException
     */
    protected void format(SignedRemEvidence signedRemEvidence, OutputStream outputStream, boolean formatted)
            throws RemEvidenceException {
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RemEvidenceException("Unable to crate a new transformer");
        }

        if (formatted) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        }
        StreamResult result = new StreamResult(outputStream);
        DOMSource source = new DOMSource(signedRemEvidence.getDocument());
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RemEvidenceException("Transformation of SignedRemEvidence to XML failed:" + e.getMessage(), e);
        }
    }

    /**
     * Parses a REM evidence instance represented as a W3C Document and creates the equivalent JAXB representation.
     * It is package protected as this is not something that should not be done outside of this package.
     *
     * @param signedRemDocument
     * @return
     * @throws RemEvidenceException
     */
    protected static JAXBElement<REMEvidenceType> toJaxb(Document signedRemDocument) throws RemEvidenceException {
        try {
            Unmarshaller unmarshaller = RemHelper.getUnmarshaller();
            return unmarshaller.unmarshal(signedRemDocument, REMEvidenceType.class);
        } catch (JAXBException e) {
            throw new RemEvidenceException("Unable to create unmarshaller");
        }
    }


    /**
     * Parses the contents of an InputStream, which is expected to supply
     * a signed REMEvidenceType in XML representation.
     * <p/>
     * Step 1: parses xml into W3C Document
     * Step 2: converts W3C Document into JAXBElement
     *
     * @param inputStream holding the xml representation of a signed REM evidence.
     * @return SignedRemEvidence
     * @throws RemEvidenceException
     */
    public SignedRemEvidence parse(InputStream inputStream) throws RemEvidenceException {

        // 1) Parses XML into W3C Document
        Document parsedDocument;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            parsedDocument = documentBuilder.parse(inputStream);
        } catch (ParserConfigurationException e) {
            throw new RemEvidenceException("Unable to create DocumentBuilder " + e.getMessage(), e);
        } catch (SAXException | IOException e) {
            throw new RemEvidenceException("Unable to parse xml input " + e.getMessage(), e);
        }

        // 2) Parses it into the JAXB representation.
        JAXBElement<REMEvidenceType> remEvidenceTypeJAXBElement = toJaxb(parsedDocument);

        return new SignedRemEvidence(remEvidenceTypeJAXBElement, parsedDocument);
    }

    public boolean isFormattedOutput() {
        return formattedOutput;
    }

    public void setFormattedOutput(boolean formattedOutput) {
        this.formattedOutput = formattedOutput;
    }
}
