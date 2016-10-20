package no.difi.vefa.peppol.sbdh;

import com.google.common.io.ByteStreams;
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

        SbdReader sbdReader = SbdReader.newInstance(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        Assert.assertEquals(sbdReader.getHeader(), SbdWriterTest.header);
        Assert.assertEquals(sbdReader.getType(), SbdReader.Type.BINARY);

        ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(sbdReader.binaryReader(), resultOutputStream);

        Assert.assertEquals(resultOutputStream.toByteArray(), document);
    }
}
