package no.difi.vefa.peppol.sbdh.util;

import com.google.common.io.BaseEncoding;

import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

public class XMLBinaryInputStream extends InputStream {

    private final XMLStreamReader xmlStreamReader;

    private final BaseEncoding baseEncoding = BaseEncoding.base64();

    private byte[] bytes;

    private int counter = 0;

    public XMLBinaryInputStream(XMLStreamReader xmlStreamReader) {
        this.xmlStreamReader = xmlStreamReader;
    }

    @Override
    public int read() throws IOException {

        // TODO

        return 0;
    }
}
