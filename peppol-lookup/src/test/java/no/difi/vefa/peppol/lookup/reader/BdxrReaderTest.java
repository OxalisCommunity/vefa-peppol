package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.common.model.*;
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
        List<DocumentTypeIdentifier> result = reader.parseDocumentIdentifiers(new FetcherResponse(getClass().getResourceAsStream("/bdxr-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);

        for (DocumentTypeIdentifier documentTypeIdentifier : result)
            logger.debug("{}", documentTypeIdentifier);
    }

    @Test
    public void serviceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(getClass().getResourceAsStream("/bdxr-servicemetadata-9908-810418052.xml"), null));

        ProcessIdentifier processIdentifier = new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver1.0");

        try {
            result.getEndpoint(processIdentifier, TransportProfile.START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(result.getEndpoint(processIdentifier, TransportProfile.AS2_1_0));

        assertEquals(result.getEndpoint(processIdentifier, TransportProfile.AS2_1_0).getCertificate().getSubjectDN().toString(), "CN=APP_1000000005, O=DIFI, C=NO");
    }

}
