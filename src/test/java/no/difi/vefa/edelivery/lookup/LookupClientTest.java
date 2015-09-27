package no.difi.vefa.edelivery.lookup;

import no.difi.vefa.edelivery.lookup.api.LookupException;
import no.difi.vefa.edelivery.lookup.fetcher.ApacheFetcher;
import no.difi.vefa.edelivery.lookup.fetcher.UrlFetcher;
import no.difi.vefa.edelivery.lookup.model.DocumentIdentifier;
import no.difi.vefa.edelivery.lookup.model.ParticipantIdentifier;
import no.difi.vefa.edelivery.lookup.model.ServiceMetadata;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class LookupClientTest {

    @Test
    public void simple() throws LookupException{
        LookupClient client = LookupClientBuilder.forProduction().fetcher(new ApacheFetcher()).build();
        List<DocumentIdentifier> documentIdentifiers = client.getDocumentIdentifiers(new ParticipantIdentifier("9908:991825827"));

        assertNotNull(documentIdentifiers);
        assertNotEquals(documentIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(new ParticipantIdentifier("9908:991825827"), new DocumentIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1"));
        assertNotNull(serviceMetadata);
    }

    @Test(enabled = false)
    public void simple9933() throws LookupException{
        LookupClient client = LookupClientBuilder.forProduction().fetcher(new UrlFetcher()).build();
        List<DocumentIdentifier> documentIdentifiers = client.getDocumentIdentifiers(new ParticipantIdentifier("9933:061828591"));

        assertNotNull(documentIdentifiers);
        assertNotEquals(documentIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(new ParticipantIdentifier("9933:061828591"), new DocumentIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"));
        assertNotNull(serviceMetadata);
    }

}
