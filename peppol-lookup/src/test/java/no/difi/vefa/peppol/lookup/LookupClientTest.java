/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

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
        List<DocumentTypeIdentifier> documentTypeIdentifiers = client.getDocumentIdentifiers(
                ParticipantIdentifier.of("9908:991825827"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(
                ParticipantIdentifier.of("9908:991825827"),
                DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                        "urn:www.cenbii.eu:transaction:biitrns010:ver2.0" +
                        ":extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1"));
        assertNotNull(serviceMetadata);
    }

    @Test
    public void simpleEndpoint() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction().build();

        Endpoint endpoint = client.getEndpoint(
                ParticipantIdentifier.of("9908:991825827"),
                DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                        "urn:www.cenbii.eu:transaction:biitrns010:ver2.0" +
                        ":extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1"),
                ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver2.0"),
                TransportProfile.AS2_1_0
        );

        assertNotNull(endpoint);
    }

    @Test
    public void simpleEndpointWithHeader() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction().build();

        Header header = Header.newInstance()
                .sender(ParticipantIdentifier.of("9908:invalid"))
                .receiver(ParticipantIdentifier.of("9908:991825827"))
                .process(ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver2.0"))
                .documentType(DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                        "urn:www.cenbii.eu:transaction:biitrns010:ver2.0" +
                        ":extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1"));

        Endpoint endpoint = client.getEndpoint(header, TransportProfile.AS2_1_0);

        assertNotNull(endpoint);
    }

    @Test(enabled = false)
    public void simple9915() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest().fetcher(new UrlFetcher()).build();
        List<DocumentTypeIdentifier> documentTypeIdentifiers =
                client.getDocumentIdentifiers(ParticipantIdentifier.of("9915:setcce-test"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);
    }

    @Test(enabled = false)
    public void simple9933() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction().fetcher(new UrlFetcher()).build();
        List<DocumentTypeIdentifier> documentTypeIdentifiers =
                client.getDocumentIdentifiers(ParticipantIdentifier.of("9933:061828591"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(
                ParticipantIdentifier.of("9933:061828591"),
                DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                        "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0" +
                        ":#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"));
        assertNotNull(serviceMetadata);
    }

    @Test(expectedExceptions = LookupException.class)
    public void noSmp() throws PeppolException {
        LookupClient client =
                LookupClientBuilder.forTest().locator(new BusdoxLocator(DynamicLocator.OPENPEPPOL_TEST)).build();

        client.getDocumentIdentifiers(ParticipantIdentifier.of("9908:no-smp"));
    }

    @Test(expectedExceptions = LookupException.class)
    public void noSml() throws PeppolException {
        LookupClient client =
                LookupClientBuilder.forTest().locator(new BusdoxLocator(DynamicLocator.OPENPEPPOL_TEST)).build();

        client.getDocumentIdentifiers(ParticipantIdentifier.of("9908:no-sml"));
    }
}
