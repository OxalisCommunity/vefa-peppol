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

package no.difi.vefa.peppol.sbdh;

import no.difi.vefa.peppol.common.api.PerformAction;
import no.difi.vefa.peppol.common.api.PerformResult;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.common.util.ExceptionUtil;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import no.difi.vefa.peppol.sbdh.util.XMLStreamPartialReaderWrapper;
import no.difi.vefa.peppol.sbdh.util.XMLTextInputStream;
import org.apache.commons.codec.binary.Base64InputStream;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class SbdReader implements Closeable {

    private XMLStreamReader reader;

    private Header header;

    public static SbdReader newInstance(final InputStream inputStream) throws SbdhException {
        return ExceptionUtil.perform(SbdhException.class, new PerformResult<SbdReader>() {
            @Override
            public SbdReader action() throws Exception {
                return newInstance(SbdhHelper.XML_INPUT_FACTORY.createXMLStreamReader(inputStream));
            }
        });
    }

    public static SbdReader newInstance(XMLStreamReader xmlStreamReader) throws SbdhException {
        return new SbdReader(xmlStreamReader);
    }

    private SbdReader(XMLStreamReader reader) throws SbdhException {
        this.reader = reader;

        try {
            // First element, SBD expected.
            if (reader.getEventType() != XMLStreamConstants.START_ELEMENT)
                reader.nextTag();

            if (!reader.getName().equals(Ns.QNAME_SBD))
                throw new SbdhException("Element 'StandardBusinessDocument' not found as first element.");

            // Read header
            reader.nextTag();
            if (!reader.getName().equals(Ns.QNAME_SBDH))
                throw new SbdhException("Element 'StandardBusinessDocumentHeader' not found " +
                        "as first element in 'StandardBusinessDocument'.");

            this.header = SbdhReader.read(reader);

            // Go to payload
            if (reader.getEventType() != XMLStreamConstants.START_ELEMENT)
                reader.nextTag();
            if (reader.getEventType() != XMLStreamConstants.START_ELEMENT)
                throw new SbdhException("Payload not found.");
        } catch (XMLStreamException e) {
            throw new SbdhException(e.getMessage(), e);
        }
    }

    public Header getHeader() {
        return header;
    }

    public Type getType() {
        if (reader.getName().equals(Ns.QNAME_BINARY_CONTENT))
            return Type.BINARY;
        else if (reader.getName().equals(Ns.QNAME_TEXT_CONTENT))
            return Type.TEXT;
        else
            return Type.XML;
    }

    public XMLStreamReader xmlReader() {
        return new XMLStreamPartialReaderWrapper(reader);
    }

    public InputStream binaryReader() throws XMLStreamException {
        return new Base64InputStream(new XMLTextInputStream(xmlReader()));
    }

    public InputStream textReader() throws XMLStreamException {
        return new XMLTextInputStream(xmlReader());
    }

    @Override
    public void close() throws IOException {
        ExceptionUtil.perform(IOException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                reader.close();
            }
        });
    }

    public enum Type {
        BINARY, TEXT, XML
    }
}
