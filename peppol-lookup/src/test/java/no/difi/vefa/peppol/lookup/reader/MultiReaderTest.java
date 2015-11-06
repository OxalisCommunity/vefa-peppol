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

public class MultiReaderTest {

    private static Logger logger = LoggerFactory.getLogger(MultiReaderTest.class);

    private MetadataReader reader = new MultiReader();

    @Test
    public void busdoxDocumentIdentifers() throws Exception {
        List<DocumentIdentifier> result = reader.parseDocumentIdentifiers(new FetcherResponse(getClass().getResourceAsStream("/busdox-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);

        for (DocumentIdentifier documentIdentifier : result)
            logger.debug("{}", documentIdentifier);
    }

    @Test
    public void bdxrDocumentIdentifers() throws Exception {
        List<DocumentIdentifier> result = reader.parseDocumentIdentifiers(new FetcherResponse(getClass().getResourceAsStream("/bdxr-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);

        for (DocumentIdentifier documentIdentifier : result)
            logger.debug("{}", documentIdentifier);
    }

    @Test
    public void busdoxServiceMetadata() throws Exception {
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

    @Test
    public void busdoxServiceMetadataMultiProcess() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(getClass().getResourceAsStream("/busdox-servicemetadata-9933-061828591.xml"), null));

        ProcessIdentifier processIdentifier1 = new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver1.0", "cenbii-procid-ubl");
        ProcessIdentifier processIdentifier2 = new ProcessIdentifier("urn:www.cenbii.eu:profile:bii46:ver1.0", "cenbii-procid-ubl");

        try {
            result.getEndpoint(processIdentifier1, TransportProfile.START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(result.getEndpoint(processIdentifier1, TransportProfile.AS2_1_0));

        assertEquals(result.getEndpoint(processIdentifier1, TransportProfile.AS2_1_0).getCertificate().getSubjectDN().toString(), "O=University of Piraeus Research Center, CN=APP_1000000088, C=GR");

        try {
            result.getEndpoint(processIdentifier2, TransportProfile.START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(result.getEndpoint(processIdentifier2, TransportProfile.AS2_1_0));

        assertEquals(result.getEndpoint(processIdentifier2, TransportProfile.AS2_1_0).getCertificate().getSubjectDN().toString(), "O=University of Piraeus Research Center, CN=APP_1000000088, C=GR");
    }

    @Test
    public void bdxrServiceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(getClass().getResourceAsStream("/bdxr-servicemetadata-9908-810418052.xml"), null));

        ProcessIdentifier processIdentifier = new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver1.0", "cenbii-procid-ubl");

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
