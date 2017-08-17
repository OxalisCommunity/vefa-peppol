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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

public class XMLBinaryInputStream extends InputStream {

    // private static final BaseEncoding baseEncoding = BaseEncoding.base64();

    private final XMLStreamReader reader;

    // private String rest = "";

    private byte[] bytes = new byte[0];

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

            bytes = reader.getText().getBytes();
            counter = 0;

            try {
                reader.next();
            } catch (XMLStreamException e) {
                throw new IOException(e.getMessage(), e);
            }
        }

        return bytes[counter++];
    }

    /*
    @Override
    public int read() throws IOException {
        if (counter == bytes.length) {
            System.out.println(reader.getEventType());

            if (!reader.isCharacters())
                return -1;

            rest = rest + reader.getText().replaceAll("[\t\n\r ]", "");

            int pos = rest.length() - (rest.length() % 4);
            bytes = baseEncoding.decode(rest.substring(0, pos));
            rest = rest.substring(pos);
            System.out.println(counter + " - " + bytes.length + " - " + rest.length());
            counter = 0;

            try {
                reader.next();
            } catch (XMLStreamException e) {
                throw new IOException(e.getMessage(), e);
            }
        }

        return bytes[counter++];
    }
    */
}
