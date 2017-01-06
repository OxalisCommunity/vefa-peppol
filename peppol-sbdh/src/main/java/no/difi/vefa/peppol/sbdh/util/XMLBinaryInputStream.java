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

    private int counter;

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
