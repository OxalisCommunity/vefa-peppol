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

package no.difi.vefa.peppol.sbdh;

import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class SbdWriter implements Closeable {

    private XMLStreamWriter xmlStreamWriter;

    public static SbdWriter newInstance(OutputStream outputStream, Header header) throws SbdhException {
        return new SbdWriter(outputStream, header);
    }

    private SbdWriter(OutputStream outputStream, Header header) throws SbdhException {
        try {
            xmlStreamWriter = SbdhHelper.XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, "UTF-8");
            initiateDocument(header);
        } catch (XMLStreamException e) {
            throw new SbdhException("Unable to initiate SBD.", e);
        }
    }

    private void initiateDocument(Header header) throws SbdhException {
        try {
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeStartElement("", "StandardBusinessDocument", SbdhHelper.NS);
            xmlStreamWriter.writeDefaultNamespace(SbdhHelper.NS);
            SbdhWriter.write(xmlStreamWriter, header);
        } catch (XMLStreamException e) {
            throw new SbdhException(e.getMessage(), e);
        }
    }

    private void finalizeDocument() throws SbdhException {
        try {
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
        } catch (XMLStreamException e) {
            throw new SbdhException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            finalizeDocument();
            xmlStreamWriter.close();
        } catch (XMLStreamException | SbdhException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
