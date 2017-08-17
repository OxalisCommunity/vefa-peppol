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

import no.difi.vefa.peppol.sbdh.Ns;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class XMLTextOutputStream extends OutputStream {

    private final XMLStreamWriter xmlStreamWriter;

    private byte[] bytes = new byte[64];

    private int counter;

    public XMLTextOutputStream(XMLStreamWriter xmlStreamWriter, String mimeType)
            throws XMLStreamException {
        this.xmlStreamWriter = xmlStreamWriter;

        xmlStreamWriter.writeStartElement("", Ns.QNAME_TEXT_CONTENT.getLocalPart(), Ns.EXTENSION);
        xmlStreamWriter.writeDefaultNamespace(Ns.EXTENSION);
        xmlStreamWriter.writeAttribute("mimeType", mimeType);
    }

    @Override
    public void write(int b) throws IOException {
        bytes[counter++] = (byte) b;

        if (counter == bytes.length) {
            try {
                xmlStreamWriter.writeCharacters(new String(bytes));
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
                xmlStreamWriter.writeCharacters(new String(Arrays.copyOf(bytes, counter)));

            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
