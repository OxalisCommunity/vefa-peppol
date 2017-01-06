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

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        SbdWriter sbdWriter = SbdWriter.newInstance(byteArrayOutputStream, SbdWriterTest.header);
        try (InputStream inputStream = new ByteArrayInputStream(document);
             OutputStream outputStream = sbdWriter.binaryWriter("application/xml")) {
            ByteStreams.copy(inputStream, outputStream);
        }
        sbdWriter.close();

        try (SbdReader sbdReader = SbdReader.newInstance(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))) {

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
                "<StandardBusinessDocument xmlns=\"%s\"><Header></Header></StandardBusinessDocument>", Ns.SBDH).getBytes()));
    }
}
