/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.sbdh.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderWrapper implements XMLStreamReader {

    private XMLStreamReader xmlStreamReader;

    public XMLStreamReaderWrapper(XMLStreamReader xmlStreamReader) {
        this.xmlStreamReader = xmlStreamReader;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return xmlStreamReader.getProperty(name);
    }

    @Override
    public int next() throws XMLStreamException {
        return xmlStreamReader.next();
    }

    @Override
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        xmlStreamReader.require(type, namespaceURI, localName);
    }

    @Override
    public String getElementText() throws XMLStreamException {
        return xmlStreamReader.getElementText();
    }

    @Override
    public int nextTag() throws XMLStreamException {
        return xmlStreamReader.nextTag();
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return xmlStreamReader.hasNext();
    }

    @Override
    public void close() throws XMLStreamException {
        // No action.
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return xmlStreamReader.getNamespaceURI(prefix);
    }

    @Override
    public boolean isStartElement() {
        return xmlStreamReader.isStartElement();
    }

    @Override
    public boolean isEndElement() {
        return xmlStreamReader.isEndElement();
    }

    @Override
    public boolean isCharacters() {
        return xmlStreamReader.isCharacters();
    }

    @Override
    public boolean isWhiteSpace() {
        return xmlStreamReader.isWhiteSpace();
    }

    @Override
    public String getAttributeValue(String namespaceURI, String localName) {
        return xmlStreamReader.getAttributeValue(namespaceURI, localName);
    }

    @Override
    public int getAttributeCount() {
        return xmlStreamReader.getAttributeCount();
    }

    @Override
    public QName getAttributeName(int index) {
        return xmlStreamReader.getAttributeName(index);
    }

    @Override
    public String getAttributeNamespace(int index) {
        return xmlStreamReader.getAttributeNamespace(index);
    }

    @Override
    public String getAttributeLocalName(int index) {
        return xmlStreamReader.getAttributeLocalName(index);
    }

    @Override
    public String getAttributePrefix(int index) {
        return xmlStreamReader.getAttributePrefix(index);
    }

    @Override
    public String getAttributeType(int index) {
        return xmlStreamReader.getAttributeType(index);
    }

    @Override
    public String getAttributeValue(int index) {
        return xmlStreamReader.getAttributeValue(index);
    }

    @Override
    public boolean isAttributeSpecified(int index) {
        return xmlStreamReader.isAttributeSpecified(index);
    }

    @Override
    public int getNamespaceCount() {
        return xmlStreamReader.getNamespaceCount();
    }

    @Override
    public String getNamespacePrefix(int index) {
        return xmlStreamReader.getNamespacePrefix(index);
    }

    @Override
    public String getNamespaceURI(int index) {
        return xmlStreamReader.getNamespaceURI(index);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return xmlStreamReader.getNamespaceContext();
    }

    @Override
    public int getEventType() {
        return xmlStreamReader.getEventType();
    }

    @Override
    public String getText() {
        return xmlStreamReader.getText();
    }

    @Override
    public char[] getTextCharacters() {
        return xmlStreamReader.getTextCharacters();
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        return xmlStreamReader.getTextCharacters(sourceStart, target, targetStart, length);
    }

    @Override
    public int getTextStart() {
        return xmlStreamReader.getTextStart();
    }

    @Override
    public int getTextLength() {
        return xmlStreamReader.getTextLength();
    }

    @Override
    public String getEncoding() {
        return xmlStreamReader.getEncoding();
    }

    @Override
    public boolean hasText() {
        return xmlStreamReader.hasText();
    }

    @Override
    public Location getLocation() {
        return xmlStreamReader.getLocation();
    }

    @Override
    public QName getName() {
        return xmlStreamReader.getName();
    }

    @Override
    public String getLocalName() {
        return xmlStreamReader.getLocalName();
    }

    @Override
    public boolean hasName() {
        return xmlStreamReader.hasName();
    }

    @Override
    public String getNamespaceURI() {
        return xmlStreamReader.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return xmlStreamReader.getPrefix();
    }

    @Override
    public String getVersion() {
        return xmlStreamReader.getVersion();
    }

    @Override
    public boolean isStandalone() {
        return xmlStreamReader.isStandalone();
    }

    @Override
    public boolean standaloneSet() {
        return xmlStreamReader.standaloneSet();
    }

    @Override
    public String getCharacterEncodingScheme() {
        return xmlStreamReader.getCharacterEncodingScheme();
    }

    @Override
    public String getPITarget() {
        return xmlStreamReader.getPITarget();
    }

    @Override
    public String getPIData() {
        return xmlStreamReader.getPIData();
    }
}
