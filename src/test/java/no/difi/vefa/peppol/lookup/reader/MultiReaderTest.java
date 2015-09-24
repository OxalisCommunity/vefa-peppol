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

public class MultiReaderTest {

    private static Logger logger = LoggerFactory.getLogger(MultiReaderTest.class);

    private MetadataReader reader = new MultiReader();

    @Test
    public void busdoxDocumentIdentifers() throws Exception {
        List<DocumentIdentifier> result = reader.parseDocumentIdentifiers(new FetcherResponse(getClass().getResourceAsStream("/busdox-servicegroup-991825827.xml"), null));

        assertEquals(result.size(), 7);

        for (DocumentIdentifier documentIdentifier : result)
            logger.debug("{}", documentIdentifier);
    }

    @Test
    public void busdoxServiceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(getClass().getResourceAsStream("/busdox-servicemetadata-991825827.xml"), null));

        assertNull(result.getEndpoint("busdox-transport-start"));
        assertNotNull(result.getEndpoint("busdox-transport-as2-ver1p0"));

        assertEquals(result.getEndpoint("busdox-transport-as2-ver1p0").getCertificate().getSubjectDN().toString(), "O=EVRY AS, CN=APP_1000000025, C=NO");
    }

}
