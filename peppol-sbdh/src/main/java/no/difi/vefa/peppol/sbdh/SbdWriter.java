/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
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

import no.difi.vefa.peppol.common.api.PerformAction;
import no.difi.vefa.peppol.common.api.PerformResult;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.common.util.ExceptionUtil;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import no.difi.vefa.peppol.sbdh.util.XMLBinaryOutputStream;
import no.difi.vefa.peppol.sbdh.util.XMLStreamWriterWrapper;
import no.difi.vefa.peppol.sbdh.util.XMLTextOutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class SbdWriter implements Closeable {

    private XMLStreamWriter writer;

    public static SbdWriter newInstance(OutputStream outputStream, Header header) throws SbdhException {
        return new SbdWriter(outputStream, header);
    }

    private SbdWriter(final OutputStream outputStream, Header header) throws SbdhException {
        writer = ExceptionUtil.perform(SbdhException.class, "Unable to initiate SBD.", new PerformResult<XMLStreamWriter>() {
            @Override
            public XMLStreamWriter action() throws Exception {
                return SbdhHelper.XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, "UTF-8");
            }
        });

        initiateDocument(header);
    }

    private void initiateDocument(final Header header) throws SbdhException {
        ExceptionUtil.perform(SbdhException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                writer.writeStartDocument("UTF-8", "1.0");
                writer.writeStartElement("", Ns.QNAME_SBD.getLocalPart(), Ns.SBDH);
                writer.writeDefaultNamespace(Ns.SBDH);
                SbdhWriter.write(writer, header);
            }
        });
    }

    public XMLStreamWriter xmlWriter() {
        return new XMLStreamWriterWrapper(writer);
    }

    public OutputStream binaryWriter(String mimeType) throws XMLStreamException {
        return binaryWriter(mimeType, null);
    }

    public OutputStream binaryWriter(String mimeType, String encoding) throws XMLStreamException {
        return new XMLBinaryOutputStream(xmlWriter(), mimeType, encoding);
    }

    public OutputStream textWriter(String mimeType) throws XMLStreamException {
        return new XMLTextOutputStream(xmlWriter(), mimeType);
    }

    private void finalizeDocument() throws SbdhException {
        ExceptionUtil.perform(SbdhException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                writer.writeEndElement();
                writer.writeEndDocument();
            }
        });
    }

    @Override
    public void close() throws IOException {
        ExceptionUtil.perform(IOException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                finalizeDocument();
                writer.close();
            }
        });
    }
}
