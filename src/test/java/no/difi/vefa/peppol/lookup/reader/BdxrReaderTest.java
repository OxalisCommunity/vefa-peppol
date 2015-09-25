package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.lookup.model.DocumentIdentifier;
import no.difi.vefa.peppol.lookup.model.FetcherResponse;
import no.difi.vefa.peppol.lookup.model.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class BdxrReaderTest {

    private static Logger logger = LoggerFactory.getLogger(BdxrReaderTest.class);

    private MetadataReader reader = new BdxrReader();

    @Test
    public void documentIdentifers() throws Exception {
        List<DocumentIdentifier> result = reader.parseDocumentIdentifiers(new FetcherResponse(getClass().getResourceAsStream("/bdxr-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);

        for (DocumentIdentifier documentIdentifier : result)
            logger.debug("{}", documentIdentifier);
    }

    @Test
    public void serviceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(getClass().getResourceAsStream("/bdxr-servicemetadata-9908-810418052.xml"), null));

        assertNull(result.getEndpoint("busdox-transport-start"));
        assertNotNull(result.getEndpoint("busdox-transport-as2-ver1p0"));

        assertEquals(result.getEndpoint("busdox-transport-as2-ver1p0").getCertificate().getSubjectDN().toString(), "CN=APP_1000000005, O=DIFI, C=NO");
    }

}
