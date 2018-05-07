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

import javax.xml.stream.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class XMLStreamUtils {

    public static void copy(Reader reader, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        XMLStreamReader xmlStreamReader = XMLInputFactory.newFactory().createXMLStreamReader(reader);
        copy(xmlStreamReader, xmlStreamWriter);
        xmlStreamReader.close();
    }

    public static void copy(XMLStreamReader xmlStreamReader, Writer writer) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(writer);
        copy(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
    }

    public static void copy(InputStream inputStream, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        XMLStreamReader xmlStreamReader = XMLInputFactory.newFactory().createXMLStreamReader(inputStream, "UTF-8");
        copy(xmlStreamReader, xmlStreamWriter);
        xmlStreamReader.close();
    }

    public static void copy(XMLStreamReader xmlStreamReader, OutputStream outputStream) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(outputStream, "UTF-8");
        copy(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
    }

    public static void copy(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        boolean hasNext;

        do {
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_DOCUMENT:
                    writer.writeStartDocument(reader.getEncoding(), reader.getVersion());
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    writer.writeEndDocument();
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    writer.writeStartElement(reader.getPrefix(), reader.getLocalName(), reader.getNamespaceURI());

                    for (int i = 0; i < reader.getNamespaceCount(); i++)
                        writer.writeNamespace(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        String prefix = reader.getAttributePrefix(i);
                        if (prefix == null || "".equals(prefix))
                            writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                        else
                            writer.writeAttribute(prefix, reader.getAttributeNamespace(i),
                                    reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    writer.writeEndElement();
                    break;

                case XMLStreamConstants.CHARACTERS:
                    writer.writeCharacters(reader.getText());
                    break;
                case XMLStreamConstants.CDATA:
                    writer.writeCData(reader.getText());
                    break;
            }

            hasNext = reader.hasNext();
            if (hasNext)
                reader.next();
        } while (hasNext);
    }
}
