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

import com.google.common.io.BaseEncoding;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class XMLBinaryOutputStream extends OutputStream {

    private final BaseEncoding baseEncoding = BaseEncoding.base64().withSeparator("\n", 76);

    private final XMLStreamWriter xmlStreamWriter;

    private byte[] bytes = new byte[3 * 20];

    private int counter;

    public XMLBinaryOutputStream(XMLStreamWriter xmlStreamWriter, String mimeType, String encoding) throws XMLStreamException {
        this.xmlStreamWriter = xmlStreamWriter;

        xmlStreamWriter.writeStartElement("", "BinaryContent", "http://peppol.eu/xsd/ticc/envelope/1.0");
        xmlStreamWriter.writeDefaultNamespace("http://peppol.eu/xsd/ticc/envelope/1.0");
        xmlStreamWriter.writeAttribute("mimeType", mimeType);

        if (encoding != null)
            xmlStreamWriter.writeAttribute("encoding", encoding);
    }

    @Override
    public void write(int b) throws IOException {
        bytes[counter++] = (byte) b;

        if (counter == bytes.length) {
            try {
                xmlStreamWriter.writeCharacters(baseEncoding.encode(bytes));
                counter = 0;
            } catch (XMLStreamException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (counter > 0)
                xmlStreamWriter.writeCharacters(baseEncoding.encode(Arrays.copyOf(bytes, counter)));

            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
