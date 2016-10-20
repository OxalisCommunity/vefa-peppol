package no.difi.vefa.peppol.sbdh;

import com.google.common.io.ByteStreams;
import no.difi.vefa.peppol.sbdh.util.XMLStreamUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SbdReaderTest {

    @Test
    public void simpleBinary() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        SbdWriter sbdWriter = SbdWriter.newInstance(byteArrayOutputStream, SbdWriterTest.header);
        try (InputStream inputStream = getClass().getResourceAsStream("/valid-t10.xml");
             OutputStream outputStream = sbdWriter.binaryWriter("application/xml")) {
            ByteStreams.copy(inputStream, outputStream);
        }
        sbdWriter.close();

        SbdReader sbdReader = SbdReader.newInstance(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        Assert.assertEquals(sbdReader.getHeader(), SbdWriterTest.header);
        Assert.assertEquals(sbdReader.getType(), SbdReader.Type.BINARY);

        XMLStreamUtils.copy(sbdReader.xmlReader(), System.out);
    }
}
