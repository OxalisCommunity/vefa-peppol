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
