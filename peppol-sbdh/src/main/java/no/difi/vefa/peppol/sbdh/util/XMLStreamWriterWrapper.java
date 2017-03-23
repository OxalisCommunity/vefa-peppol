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

package no.difi.vefa.peppol.sbdh.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterWrapper implements XMLStreamWriter {

    private XMLStreamWriter xmlStreamWriter;

    public XMLStreamWriterWrapper(XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(localName);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        xmlStreamWriter.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        // No action.
    }

    @Override
    public void close() throws XMLStreamException {
        // No action.
    }

    @Override
    public void flush() throws XMLStreamException {
        xmlStreamWriter.flush();
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(localName, value);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
            throws XMLStreamException {
        xmlStreamWriter.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(namespaceURI, localName, value);
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        xmlStreamWriter.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        xmlStreamWriter.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        xmlStreamWriter.writeComment(data);
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        xmlStreamWriter.writeProcessingInstruction(target);
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        xmlStreamWriter.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        xmlStreamWriter.writeCData(data);
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        // No action
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        xmlStreamWriter.writeEntityRef(name);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        // No action
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        // No action
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        // No action
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(text);
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(text, start, len);
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return xmlStreamWriter.getPrefix(uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        xmlStreamWriter.setPrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        xmlStreamWriter.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        xmlStreamWriter.setNamespaceContext(context);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return xmlStreamWriter.getNamespaceContext();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return xmlStreamWriter.getProperty(name);
    }
}
