package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.lookup.model.DocumentIdentifier;
import no.difi.vefa.peppol.lookup.model.FetcherResponse;
import no.difi.vefa.peppol.lookup.model.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class BusdoxReaderTest {

    private static Logger logger = LoggerFactory.getLogger(BusdoxReaderTest.class);

    private BusdoxReader busdoxReader = new BusdoxReader();

    @Test
    public void documentIdentifers() throws Exception {
        List<DocumentIdentifier> result = busdoxReader.parseDocumentIdentifiers(new FetcherResponse(getClass().getResourceAsStream("/busdox-servicegroup-991825827.xml"), null));

        assertEquals(result.size(), 7);

        for (DocumentIdentifier documentIdentifier : result)
            logger.debug("{}", documentIdentifier);
    }

    @Test
    public void serviceMetadata() throws Exception {
        ServiceMetadata result = busdoxReader.parseServiceMetadata(new FetcherResponse(getClass().getResourceAsStream("/busdox-servicemetadata-991825827.xml"), null));

        assertNull(result.getEndpoint("busdox-transport-start"));
        assertNotNull(result.getEndpoint("busdox-transport-as2-ver1p0"));

        assertEquals(result.getEndpoint("busdox-transport-as2-ver1p0").getCertificate().getSubjectDN().toString(), "O=EVRY AS, CN=APP_1000000025, C=NO");
    }

}
