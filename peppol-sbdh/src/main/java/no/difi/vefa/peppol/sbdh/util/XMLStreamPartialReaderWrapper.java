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

package no.difi.vefa.peppol.sbdh.util;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamPartialReaderWrapper extends XMLStreamReaderWrapper {

    private int eventType = START_DOCUMENT;

    private int level = -1;

    public XMLStreamPartialReaderWrapper(XMLStreamReader xmlStreamReader) {
        super(xmlStreamReader);
    }

    @Override
    public int next() throws XMLStreamException {
        // Trigger next event
        this.eventType = eventType == START_DOCUMENT ? super.getEventType() : super.next();

        if (eventType == START_ELEMENT) {
            level++;
        } else if (eventType == END_ELEMENT) {
            level--;

            if (level == -1)
                eventType = END_DOCUMENT;
        }

        return eventType;
    }

    @Override
    public int nextTag() throws XMLStreamException {
        int eventType = next();

        while ((eventType == XMLStreamConstants.CHARACTERS && isWhiteSpace()) // skip whitespace
                || (eventType == XMLStreamConstants.CDATA && isWhiteSpace()) // skip whitespace
                || eventType == XMLStreamConstants.SPACE
                || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                || eventType == XMLStreamConstants.COMMENT
                ) {
            eventType = next();
        }

        if (eventType != XMLStreamConstants.START_ELEMENT && eventType != XMLStreamConstants.END_ELEMENT)
            throw new XMLStreamException("expected start or end tag", getLocation());

        return eventType;
    }

    @Override
    public int getEventType() {
        return eventType;
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return eventType == END_DOCUMENT ? false : super.hasNext();
    }
}
