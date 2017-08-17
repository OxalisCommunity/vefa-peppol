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

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SbdReaderTest {

    @Test
    public void simpleBinary() throws Exception {
        byte[] document = ByteStreams.toByteArray(getClass().getResourceAsStream("/valid-t10.xml"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        SbdWriter sbdWriter = SbdWriter.newInstance(baos, SbdWriterTest.header);
        try (InputStream inputStream = new ByteArrayInputStream(document);
             OutputStream outputStream = sbdWriter.binaryWriter("application/xml")) {
            ByteStreams.copy(inputStream, outputStream);
        }
        sbdWriter.close();

        try (SbdReader sbdReader = SbdReader.newInstance(new ByteArrayInputStream(baos.toByteArray()))) {

            Assert.assertEquals(sbdReader.getHeader(), SbdWriterTest.header);
            Assert.assertEquals(sbdReader.getType(), SbdReader.Type.BINARY);

            ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();
            ByteStreams.copy(sbdReader.binaryReader(), resultOutputStream);

            Assert.assertEquals(resultOutputStream.toByteArray(), document);
        }
    }

    @Test(expectedExceptions = SbdhException.class)
    public void exceptionOnNonXML() throws Exception {
        SbdReader.newInstance(new ByteArrayInputStream("{json: true}".getBytes()));
    }

    @Test(expectedExceptions = SbdhException.class)
    public void exceptionOnNotSBD() throws Exception {
        SbdReader.newInstance(new ByteArrayInputStream("<StandardBusinessDocument/>".getBytes()));
    }

    @Test(expectedExceptions = SbdhException.class)
    public void exceptionOnNotSBDH() throws Exception {
        SbdReader.newInstance(new ByteArrayInputStream(String.format(
                "<StandardBusinessDocument xmlns=\"%s\"><Header></Header></StandardBusinessDocument>",
                Ns.SBDH).getBytes()));
    }

    @Test
    public void simple() throws Exception {
        byte[] expected = ByteStreams.toByteArray(getClass().getResourceAsStream("/iso20022/iso20022-outer.asice"));

        // Create SBD
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try (SbdWriter sbdWriter = SbdWriter.newInstance(result, SbdhReader.read(getClass().getResourceAsStream("/iso20022/sbdh.xml")))) {
            try (OutputStream binaryOutputStream = sbdWriter.binaryWriter("application/vnd.etsi.asic-e+zip", "UTF-8")) {
                ByteStreams.copy(new ByteArrayInputStream(expected), binaryOutputStream);
            }
        }

        // Parse SBD
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        try (SbdReader sbdReader = SbdReader.newInstance(new ByteArrayInputStream(result.toByteArray()))) {
            try (InputStream binaryInputStream = sbdReader.binaryReader()) {
                ByteStreams.copy(binaryInputStream, actual);
            }
        }

        // Verify
        BaseEncoding encoding = BaseEncoding.base64().withSeparator("\r\n", 40);
        Assert.assertEquals(
                encoding.encode(actual.toByteArray()),
                encoding.encode(expected)
        );
    }
}
