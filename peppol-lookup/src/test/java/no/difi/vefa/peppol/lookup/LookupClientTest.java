package no.difi.vefa.peppol.lookup;

import no.difi.vefa.peppol.common.lang.PeppolException;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.fetcher.ApacheFetcher;
import no.difi.vefa.peppol.lookup.fetcher.UrlFetcher;
import no.difi.vefa.peppol.lookup.locator.BusdoxLocator;
import no.difi.vefa.peppol.lookup.locator.DynamicLocator;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class LookupClientTest {

    @Test(enabled = false)
    public void simple() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction().fetcher(new ApacheFetcher()).build();
        List<DocumentTypeIdentifier> documentTypeIdentifiers = client.getDocumentIdentifiers(new ParticipantIdentifier("9908:991825827"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(new ParticipantIdentifier("9908:991825827"), new DocumentTypeIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1"));
        assertNotNull(serviceMetadata);
    }

    @Test
    public void simpleEndpoint() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction().build();

        Endpoint endpoint = client.getEndpoint(
                new ParticipantIdentifier("9908:991825827"),
                new DocumentTypeIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1"),
                new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver2.0"),
                TransportProfile.AS2_1_0
        );

        assertNotNull(endpoint);
    }

    @Test(enabled = false)
    public void simple9915() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest().fetcher(new UrlFetcher()).build();
        List<DocumentTypeIdentifier> documentTypeIdentifiers = client.getDocumentIdentifiers(new ParticipantIdentifier("9915:setcce-test"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);
    }

    @Test(enabled = false)
    public void simple9933() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction().fetcher(new UrlFetcher()).build();
        List<DocumentTypeIdentifier> documentTypeIdentifiers = client.getDocumentIdentifiers(new ParticipantIdentifier("9933:061828591"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(new ParticipantIdentifier("9933:061828591"), new DocumentTypeIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"));
        assertNotNull(serviceMetadata);
    }

    @Test(expectedExceptions = LookupException.class)
    public void noSmp() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest().locator(new BusdoxLocator(DynamicLocator.OPENPEPPOL_TEST)).build();

        client.getDocumentIdentifiers(new ParticipantIdentifier("9908:no-smp"));
    }

    @Test(expectedExceptions = LookupException.class)
    public void noSml() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest().locator(new BusdoxLocator(DynamicLocator.OPENPEPPOL_TEST)).build();

        client.getDocumentIdentifiers(new ParticipantIdentifier("9908:no-sml"));
    }
}
