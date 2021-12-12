/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package network.oxalis.vefa.peppol.lookup;

import com.github.tomakehurst.wiremock.WireMockServer;
import network.oxalis.vefa.peppol.common.lang.PeppolException;
import network.oxalis.vefa.peppol.common.model.*;
import network.oxalis.vefa.peppol.lookup.api.FetcherResponse;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.NotFoundException;
import network.oxalis.vefa.peppol.lookup.fetcher.ApacheFetcher;
import network.oxalis.vefa.peppol.lookup.fetcher.UrlFetcher;
import network.oxalis.vefa.peppol.lookup.locator.BusdoxLocator;
import network.oxalis.vefa.peppol.mode.Mode;
import network.oxalis.vefa.peppol.security.api.CertificateValidator;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.testng.Assert.*;

public class LookupClientTest {

    private final Mode testMode = Mode.of("TEST");

    @Test
    public void simple() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction()
                .fetcher(ApacheFetcher.class)
                .build();

        List<DocumentTypeIdentifier> documentTypeIdentifiers = client.getDocumentIdentifiers(
                ParticipantIdentifier.of("9908:810418052"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(
                ParticipantIdentifier.of("9908:810418052"),
                DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2::Catalogue##" +
                        "urn:www.cenbii.eu:transaction:biitrns019:ver2.0:extended:urn:www.peppol.eu:bis:peppol1a:ver2.0::2.1"));
        assertNotNull(serviceMetadata);
    }

    @Test
    public void simpleHeader() throws PeppolException {
        LookupClient client = LookupClientBuilder.forMode(testMode)
                .fetcher(ApacheFetcher.class)
                .build();

        Endpoint endpoint = client.getEndpoint(
                Header.newInstance()
                        .receiver(ParticipantIdentifier.of("9915:helger"))
                        .documentType(DocumentTypeIdentifier.of(
                                "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                                        "urn:cen.eu:en16931:2017#compliant#" +
                                        "urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"))
                        .process(ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")),
                TransportProfile.PEPPOL_AS4_2_0);

        assertNotNull(endpoint);
    }

    @Test
    public void simpleEndpoint() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest()
                .certificateValidator(CertificateValidator.EMPTY)
                .build();

        Endpoint endpoint = client.getEndpoint(
                ParticipantIdentifier.of("9915:helger"),
                DocumentTypeIdentifier.of(
                        "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                                "urn:cen.eu:en16931:2017#compliant#" +
                                "urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"),
                ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"),
                TransportProfile.PEPPOL_AS4_2_0
        );

        assertNotNull(endpoint);
    }

    @Test
    public void simpleEndpointWithHeader() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest()
                .certificateValidator(CertificateValidator.EMPTY)
                .build();

        Header header = Header.newInstance()
                .sender(ParticipantIdentifier.of("9908:invalid"))
                .receiver(ParticipantIdentifier.of("9915:helger"))
                .process(ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"))
                .documentType(DocumentTypeIdentifier.of(
                        "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                                "urn:cen.eu:en16931:2017#compliant#" +
                                "urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"));

        Endpoint endpoint = client.getEndpoint(header, TransportProfile.PEPPOL_AS4_2_0);

        assertNotNull(endpoint);
    }

    @Test(enabled = false)
    public void simple9915() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest()
                .fetcher(UrlFetcher.class)
                .build();

        List<DocumentTypeIdentifier> documentTypeIdentifiers =
                client.getDocumentIdentifiers(ParticipantIdentifier.of("9915:setcce-test"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);
    }

    @Test(enabled = false)
    public void simple9933() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction()
                .fetcher(UrlFetcher.class)
                .build();

        List<DocumentTypeIdentifier> documentTypeIdentifiers =
                client.getDocumentIdentifiers(ParticipantIdentifier.of("9933:061828591"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(
                ParticipantIdentifier.of("9933:061828591"),
                DocumentTypeIdentifier.of(
                        "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                                "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0" +
                                ":#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"));
        assertNotNull(serviceMetadata);
    }

    @Test(enabled = false, expectedExceptions = NotFoundException.class)
    public void noSmp() throws PeppolException {
        LookupClient client =
                LookupClientBuilder.forMode(testMode)
                        .locator(BusdoxLocator.class)
                        .build();

        List<DocumentTypeIdentifier> dti = client.getDocumentIdentifiers(ParticipantIdentifier.of("9908:no-smp"));
        System.out.println(dti);
    }

    @Test(enabled = false, expectedExceptions = NotFoundException.class)
    public void noSmpApache() throws PeppolException {
        LookupClient client =
                LookupClientBuilder.forMode(testMode)
                        .fetcher(ApacheFetcher.class)
                        .locator(BusdoxLocator.class)
                        .build();

        client.getDocumentIdentifiers(ParticipantIdentifier.of("9908:no-smp"));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void noSml() throws PeppolException {
        LookupClient client =
                LookupClientBuilder.forMode(testMode)
                        .locator(BusdoxLocator.class)
                        .build();

        client.getDocumentIdentifiers(ParticipantIdentifier.of("9908:no-sml"));
    }

    @Test
    /*
     In the absence of available redirects in TEST or PROD, this test is mocked using WireMock
     */
    public void wiremockRedirect() throws PeppolException, IOException {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8089));
        wireMockServer.start();

        try {
            ParticipantIdentifier participantIdentifier = ParticipantIdentifier.of("0192:991825827");
            DocumentTypeIdentifier documentTypeIdentifier = DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse##urn:fdc:peppol.eu:poacc:trns:order_response:3:extended:urn:fdc:anskaffelser.no:2019:ehf:spec:3.0::2.2");
            String url = "/iso6523-actorid-upis::0192:991825827/services/busdox-docid-qns::urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse%23%23urn:fdc:peppol.eu:poacc:trns:order_response:3:extended:urn:fdc:anskaffelser.no:2019:ehf:spec:3.0::2.2";

            wireMockServer.givenThat(get(urlEqualTo(url))
                    .inScenario("Redirect")
                    .whenScenarioStateIs("Started")
                    .willSetStateTo("Redirected")
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.SC_OK)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "text/xml")
                            .withBody(IOUtils.toByteArray(getClass().getResourceAsStream("/busdox-servicemetadata-redirect-0192-991825827.xml")))));

            wireMockServer.givenThat(get(urlEqualTo(url))
                    .inScenario("Redirect")
                    .whenScenarioStateIs("Redirected")
                    .willSetStateTo("Started")
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.SC_OK)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "text/xml")
                            .withBody(IOUtils.toByteArray(getClass().getResourceAsStream("/busdox-servicemetadata-redirected-0192-991825827.xml")))));

            URI baseUri = URI.create(wireMockServer.baseUrl());

            TestApacheFetcher metadataFetcher = new TestApacheFetcher(testMode, baseUri);
            LookupClient client = LookupClientBuilder.forMode(testMode)
                    .fetcher(metadataFetcher)
                    .build();

            ServiceMetadata serviceMetadata = client.getServiceMetadata(
                    participantIdentifier,
                    documentTypeIdentifier
            );

            assertNotNull(serviceMetadata);
            assertNotNull(serviceMetadata.getServiceInformation());

            wireMockServer.verify(2, getRequestedFor(urlEqualTo(url)));

            assertEquals(metadataFetcher.getUriList().size(), 2);
            assertEquals(metadataFetcher.getUriList().get(0), URI.create("http://B-9823154777831486f5f30f7f41385a2a.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu/iso6523-actorid-upis%3A%3A0192%3A991825827/services/busdox-docid-qns%3A%3Aurn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AOrderResponse-2%3A%3AOrderResponse%23%23urn%3Afdc%3Apeppol.eu%3Apoacc%3Atrns%3Aorder_response%3A3%3Aextended%3Aurn%3Afdc%3Aanskaffelser.no%3A2019%3Aehf%3Aspec%3A3.0%3A%3A2.2"));
            assertEquals(metadataFetcher.getUriList().get(1), URI.create("http://test-smp2.difi.no/iso6523-actorid-upis%3A%3A0192%3A991825827/services/busdox-docid-qns%3A%3Aurn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AOrderResponse-2%3A%3AOrderResponse%23%23urn%3Afdc%3Apeppol.eu%3Apoacc%3Atrns%3Aorder_response%3A3%3Aextended%3Aurn%3Afdc%3Aanskaffelser.no%3A2019%3Aehf%3Aspec%3A3.0%3A%3A2.2"));
        } finally {
            wireMockServer.stop();
        }
    }

    private static class TestApacheFetcher extends ApacheFetcher {

        private final List<URI> uriList;
        private final URI baseUri;

        public TestApacheFetcher(Mode mode, URI baseUri) {
            super(mode);
            this.baseUri = baseUri;
            uriList = new ArrayList<>();
        }

        public List<URI> getUriList() {
            return uriList;
        }

        @Override
        public FetcherResponse fetch(URI uri) throws LookupException, FileNotFoundException {
            uriList.add(uri);
            return super.fetch(replaceWithWireMock(uri));
        }

        private URI replaceWithWireMock(URI uri) {
            try {
                return new URI(baseUri.getScheme(), baseUri.getUserInfo(), baseUri.getHost(), baseUri.getPort(),
                        uri.getPath(), uri.getQuery(), uri.getFragment());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid URI!", e);
            }
        }
    }
}
