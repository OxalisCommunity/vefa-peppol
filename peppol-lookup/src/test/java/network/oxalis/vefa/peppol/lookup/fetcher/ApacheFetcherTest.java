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

package network.oxalis.vefa.peppol.lookup.fetcher;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import network.oxalis.vefa.peppol.lookup.api.*;
import network.oxalis.vefa.peppol.mode.Mode;
import org.testng.annotations.*;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.testng.Assert.*;

@Slf4j
public class ApacheFetcherTest {

    private MetadataFetcher fetcher;
    private WireMockServer wireMock;

    @BeforeClass
    public void setUp() {
        fetcher = new ApacheFetcher(Mode.of("TEST"));
        wireMock = new WireMockServer(wireMockConfig().dynamicPort());
        wireMock.start();
    }

    @AfterClass
    public void tearDown() {
        wireMock.stop();
    }

    @BeforeMethod
    public void resetStubs() {
        wireMock.resetAll();
    }

    private List<URI> stubAndUri(int statusCode) {
        wireMock.stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse().withStatus(statusCode)));
        return Collections.singletonList(URI.create(wireMock.baseUrl() + "/test"));
    }


    @Test(expectedExceptions = LookupException.class)
    public void simpleTimeout() throws LookupException {
        List<URI> uriList = new ArrayList<URI>();
        uriList.add(URI.create("http://invalid.example.com/"));
        fetcher.fetch(uriList);
    }

    @Test(expectedExceptions = {LookupException.class})
    public void simple404() throws LookupException {
        List<URI> uriList = new ArrayList<URI>();
        uriList.add(URI.create("http://httpstat.us/404"));
        fetcher.fetch(uriList);
    }

    @Test(expectedExceptions = LookupException.class)
    public void simple500() throws LookupException {
        List<URI> uriList = new ArrayList<URI>();
        uriList.add(URI.create("http://httpstat.us/500"));
        fetcher.fetch(uriList);
    }

    @Test(expectedExceptions = LookupException.class)
    public void simpleNullPointer() throws LookupException {
        fetcher.fetch(null);
    }

    // ════════════════════════════════════════════════════════════
    // Data Providers
    // ════════════════════════════════════════════════════════════

    @DataProvider(name = "resourceNotFoundCodes")
    public Object[][] resourceNotFoundCodes() {
        return new Object[][]{
                {404, "Participant metadata not found"},
        };
    }

    @DataProvider(name = "infrastructureErrorCodes")
    public Object[][] infrastructureErrorCodes() {
        return new Object[][]{
                {500, "SMP server error (HTTP 500)"},
                {502, "SMP server error (HTTP 502)"},
                {503, "SMP server error (HTTP 503)"},
                {504, "SMP server error (HTTP 504)"},
                {429, "Too many requests to SMP"},
        };
    }

    @DataProvider(name = "unmappedHttpCodes")
    public Object[][] unmappedHttpCodes() {
        return new Object[][]{
                {301, "Received HTTP status code 301"},
                {400, "Received HTTP status code 400"},
                {401, "Received HTTP status code 401"},
                {403, "Received HTTP status code 403"},
                {405, "Received HTTP status code 405"},

        };
    }


    // ════════════════════════════════════════════════════════════
    // HTTP Status Code → Exception Type Mapping
    // ════════════════════════════════════════════════════════════

    @Test(dataProvider = "resourceNotFoundCodes", groups = "http-status-mapping")
    public void fetch_httpStatus_throwsPeppolResourceException(int statusCode, String expectedMsg) throws LookupException {

        log.info("Testing HTTP status code {} for expected PeppolResourceException", statusCode);

        // Arrange
        List<URI> uriList = stubAndUri(statusCode);

        try {
            // Act
            fetcher.fetch(uriList);
            fail(String.format("Expected PeppolResourceException for HTTP %d", statusCode));
        } catch (PeppolResourceException e) {
            // Assert
            assertTrue(e.getMessage().contains(expectedMsg),
                    String.format("HTTP %d — expected message containing '%s' but got: %s", statusCode, expectedMsg, e.getMessage()));
            assertNoFileNotFoundException(e, statusCode);
        } catch (LookupException e) {
            fail(String.format("HTTP %d — expected PeppolResourceException but got %s: %s", statusCode, e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Test(dataProvider = "infrastructureErrorCodes", groups = "http-status-mapping")
    public void fetch_httpStatus_throwsPeppolInfrastructureException(int statusCode, String expectedMsg) throws LookupException {

        log.info("Testing HTTP status code {} for expected PeppolInfrastructureException", statusCode);

        // Arrange
        List<URI> uriList = stubAndUri(statusCode);

        try {
            // Act
            fetcher.fetch(uriList);
            fail(String.format("Expected PeppolInfrastructureException for HTTP %d", statusCode));
        } catch (PeppolInfrastructureException e) {
            // Assert
            assertTrue(e.getMessage().contains(expectedMsg),
                    String.format("HTTP %d — expected message containing '%s' but got: %s", statusCode, expectedMsg, e.getMessage()));
            assertNoFileNotFoundException(e, statusCode);
        } catch (LookupException e) {
            fail(String.format("HTTP %d — expected PeppolInfrastructureException but got %s: %s", statusCode, e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Test(dataProvider = "unmappedHttpCodes", groups = "http-status-mapping")
    public void fetch_httpStatus_unmappedCodes_throwGenericLookupException(int statusCode, String expectedMsg) throws LookupException {

        log.info("Testing HTTP status code {} for expected generic LookupException", statusCode);
        // Arrange
        List<URI> uriList = stubAndUri(statusCode);

        try {
            // Act
            fetcher.fetch(uriList);
            fail(String.format("Expected LookupException for HTTP %d", statusCode));
        } catch (PeppolResourceException e) {
            fail(String.format("HTTP %d — must NOT throw PeppolResourceException. Got: %s", statusCode, e.getMessage()));
        } catch (PeppolInfrastructureException e) {
            fail(String.format("HTTP %d — must NOT throw PeppolInfrastructureException. Got: %s", statusCode, e.getMessage()));
        } catch (LookupException e) {

            // Assert
            assertTrue(e.getMessage().contains(expectedMsg),
                    String.format("HTTP %d — expected message containing '%s' but got: %s", statusCode, expectedMsg, e.getMessage()));
            assertNoFileNotFoundException(e, statusCode);
        }
    }

    // ════════════════════════════════════════════════════════════
    // Network Failure Tests
    // ════════════════════════════════════════════════════════════

    @Test
    public void fetch_unreachableHost_throwsNetworkFailureException() {

        // Arrange
        List<URI> uriList = Collections.singletonList(URI.create("http://invalid.example.com/"));

        try {
            // Act
            fetcher.fetch(uriList);
            fail("Expected NetworkFailureException for unreachable host");
        } catch (NetworkFailureException e) {
            // Assert
            assertNotNull(e.getMessage());
            assertNoFileNotFoundException(e, -1);
        } catch (LookupException e) {
            fail(String.format("Expected NetworkFailureException but got %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════════
    // Multi-URI Fallback Behaviour
    // ════════════════════════════════════════════════════════════

    @Test
    public void fetch_multiUri_firstFailsSecondSucceeds_returnsResponse() throws LookupException {

        // Arrange
        wireMock.stubFor(get(urlEqualTo("/fail")).willReturn(aResponse().withStatus(503)));
        wireMock.stubFor(get(urlEqualTo("/ok")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody("<root/>")));

        List<URI> uriList = List.of(URI.create(wireMock.baseUrl() + "/fail"), URI.create(wireMock.baseUrl() + "/ok"));

        // Act
        FetcherResponse response = fetcher.fetch(uriList);

        // Assert
        assertNotNull(response);
    }

    @Test
    public void fetch_multiUri_allFail_lastExceptionTypePreserved() {
        // Arrange
        wireMock.stubFor(get(urlEqualTo("/first")).willReturn(aResponse().withStatus(503)));
        wireMock.stubFor(get(urlEqualTo("/second")).willReturn(aResponse().withStatus(404)));

        List<URI> uriList = List.of(URI.create(wireMock.baseUrl() + "/first"), URI.create(wireMock.baseUrl() + "/second"));

        try {
            // Act
            fetcher.fetch(uriList);
            fail("Expected exception to be thrown when all URIs fail");
        } catch (PeppolResourceException e) {
            // Assert
            assertTrue(e.getMessage().contains("not found"), "Expected 'not found' in message but got: " + e.getMessage());
        } catch (LookupException e) {
            fail(String.format("Expected PeppolResourceException (last URI was 404) but got %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }


    // ════════════════════════════════════════════════════════════
    // FileNotFoundException Regression Guard
    // ════════════════════════════════════════════════════════════
    private void assertNoFileNotFoundException(Throwable thrown, int statusCode) {
        Throwable current = thrown;
        while (current != null) {
            assertFalse(current instanceof FileNotFoundException,
                    "FileNotFoundException must NEVER appear in the exception chain" +
                            (statusCode > 0 ? " for HTTP " + statusCode : "") +
                            ". Found at: " + current.getClass().getName() + ": " + current.getMessage());
            current = current.getCause();
        }
    }

}
