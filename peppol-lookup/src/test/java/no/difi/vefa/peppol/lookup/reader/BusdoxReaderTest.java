package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.common.api.EndpointNotFoundException;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.common.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class BusdoxReaderTest {

    private static Logger logger = LoggerFactory.getLogger(BusdoxReaderTest.class);

    private MetadataReader reader = new BusdoxReader();

    @Test
    public void documentIdentifers() throws Exception {
        List<DocumentIdentifier> result = reader.parseDocumentIdentifiers(new FetcherResponse(getClass().getResourceAsStream("/busdox-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);

        for (DocumentIdentifier documentIdentifier : result)
            logger.debug("{}", documentIdentifier);
    }

    @Test
    public void serviceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(getClass().getResourceAsStream("/busdox-servicemetadata-9908-991825827.xml"), null));

        ProcessIdentifier processIdentifier = new ProcessIdentifier("urn:www.cenbii.eu:profile:bii05:ver2.0", "cenbii-procid-ubl");

        try {
            result.getEndpoint(processIdentifier, TransportProfile.START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(result.getEndpoint(processIdentifier, TransportProfile.AS2_1_0));

        assertEquals(result.getEndpoint(processIdentifier, TransportProfile.AS2_1_0).getCertificate().getSubjectDN().toString(), "O=EVRY AS, CN=APP_1000000025, C=NO");
    }

}
