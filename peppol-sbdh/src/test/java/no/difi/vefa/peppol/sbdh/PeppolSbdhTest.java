package no.difi.vefa.peppol.sbdh;

import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author erlend
 */
public class PeppolSbdhTest {

    @Test
    public void version100() throws IOException, SbdhException {
        try (InputStream inputStream = getClass().getResourceAsStream("/peppol-sbdh-1.00.xml")) {
            Header header = SbdhReader.read(inputStream);

            Assert.assertEquals(
                    header.getSender(),
                    ParticipantIdentifier.of(
                            "9908:810418052",
                            ParticipantIdentifier.DEFAULT_SCHEME
                    ));
            Assert.assertEquals(
                    header.getReceiver(),
                    ParticipantIdentifier.of(
                            "9908:810418052",
                            ParticipantIdentifier.DEFAULT_SCHEME
                    ));
            Assert.assertEquals(
                    header.getDocumentType(),
                    DocumentTypeIdentifier.of(
                            "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1",
                            DocumentTypeIdentifier.DEFAULT_SCHEME
                    ));
            Assert.assertEquals(
                    header.getProcess(),
                    ProcessIdentifier.of(
                            "urn:www.cenbii.eu:profile:bii04:ver2.0",
                            ProcessIdentifier.DEFAULT_SCHEME
                    ));
            Assert.assertEquals(
                    header.getCreationTimestamp(),
                    new Date(1512551825734L)
            );
            Assert.assertEquals(
                    header.getIdentifier(),
                    InstanceIdentifier.of("7f58475e-a9cc-4386-904c-cf09c2662c19")
            );
            Assert.assertEquals(
                    header.getInstanceType().getStandard(),
                    "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"
            );
            Assert.assertEquals(
                    header.getInstanceType().getType(),
                    "Invoice"
            );
            Assert.assertEquals(
                    header.getInstanceType().getVersion(),
                    "2.1"
            );
        }
    }

    @Test
    public void version101() throws IOException, SbdhException {
        try (InputStream inputStream = getClass().getResourceAsStream("/peppol-sbdh-1.01.xml")) {
            Header header = SbdhReader.read(inputStream);

            Assert.assertEquals(
                    header.getSender(),
                    ParticipantIdentifier.of(
                            "9908:810418052",
                            ParticipantIdentifier.DEFAULT_SCHEME
                    ));
            Assert.assertEquals(
                    header.getReceiver(),
                    ParticipantIdentifier.of(
                            "9908:810418052",
                            ParticipantIdentifier.DEFAULT_SCHEME
                    ));
            Assert.assertEquals(
                    header.getDocumentType(),
                    DocumentTypeIdentifier.of(
                            "urn:cen.eu:en16931:2017",
                            Scheme.of("busdox-docid-edifact")
                    ));
            Assert.assertEquals(
                    header.getProcess(),
                    ProcessIdentifier.NO_PROCESS);
            Assert.assertEquals(
                    header.getCreationTimestamp(),
                    new Date(1512551825734L)
            );
            Assert.assertEquals(
                    header.getIdentifier(),
                    InstanceIdentifier.of("7f58475e-a9cc-4386-904c-cf09c2662c19")
            );
            Assert.assertEquals(
                    header.getInstanceType().getStandard(),
                    "EDIFACT"
            );
            Assert.assertEquals(
                    header.getInstanceType().getType(),
                    "INVOIC"
            );
            Assert.assertEquals(
                    header.getInstanceType().getVersion(),
                    "D.14B"
            );
        }
    }
}
