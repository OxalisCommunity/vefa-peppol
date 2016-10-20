package no.difi.vefa.peppol.sbdh.util;

import com.google.common.io.BaseEncoding;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

public class XMLBinaryInputStream extends InputStream {

    private final BaseEncoding baseEncoding = BaseEncoding.base64();

    private final XMLStreamReader reader;

    private String rest = "";

    private byte[] bytes = rest.getBytes();

    private int counter = 0;

    public XMLBinaryInputStream(XMLStreamReader reader) throws XMLStreamException {
        this.reader = reader;

        while (!reader.isCharacters())
            reader.next();
    }

    @Override
    public int read() throws IOException {
        if (counter == bytes.length) {
            if (!reader.isCharacters())
                return -1;

            rest = rest + reader.getText().replaceAll("[\t\n\r ]", "");

            int pos = rest.length() - (rest.length() % 4);
            bytes = baseEncoding.decode(rest.substring(0, pos));
            rest = rest.substring(pos);
            counter = 0;

            try {
                reader.next();
            } catch (XMLStreamException e) {
                throw new IOException(e.getMessage(), e);
            }
        }

        return bytes[counter++];
    }
}
