package network.oxalis.vefa.peppol.lookup.provider;

import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.List;

public class DefaultProviderTest {

    private DefaultProvider provider;
    private ParticipantIdentifier participant;
    private DocumentTypeIdentifier busdoxDocumentType;
    private DocumentTypeIdentifier pintWildCardDocumentType;

    @BeforeClass
    public void setup() {
        provider = new DefaultProvider();
        participant = ParticipantIdentifier.of("0204:api-global");
        busdoxDocumentType = DocumentTypeIdentifier.of(
                "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                        "urn:cen.eu:en16931:2017#compliant#" +
                        "urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1", DocumentTypeIdentifier.BUSDOX_DOCID_QNS_SCHEME);

        pintWildCardDocumentType = DocumentTypeIdentifier.of(
                "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                        "urn:peppol:pint:billing-1@jp-1::2.1",
                DocumentTypeIdentifier.PEPPOL_DOCTYPE_WILDCARD_SCHEME
        );
    }

    @DataProvider(name = "smpUrls")
    public Object[][] smpUrls() {
        String encodedParticipant = participant.urlencoded();
        return new Object[][]{
                {"https://peppol.domain.com", "https://peppol.domain.com/" + encodedParticipant},
                {"https://peppol.domain.com/", "https://peppol.domain.com/" + encodedParticipant},
                {"https://peppol.domain.com/smp", "https://peppol.domain.com/smp/" + encodedParticipant},
                {"https://peppol.domain.com/smp/", "https://peppol.domain.com/smp/" + encodedParticipant}
        };
    }
    @Test(dataProvider = "smpUrls")
    public void testResolveDocumentIdentifiers(String smpUrl, String expectedStart) {
        URI location = URI.create(smpUrl);
        List<URI> uris = provider.resolveDocumentIdentifiers(location, participant);
        Assert.assertEquals(uris.size(), 1, "Expected exactly one URI for base: " + smpUrl);
        String actual = uris.get(0).toString();
        Assert.assertTrue(actual.startsWith(expectedStart), "Resolved URI does not start as expected. Got: " + actual);
    }

    @DataProvider(name = "smpUrlsWithServices")
    public Object[][] smpUrlsWithServices() {
        String encodedParticipant = participant.urlencoded();
        String encodedBusdoxDocType = busdoxDocumentType.urlencoded();

        return new Object[][]{
                {"https://peppol.domain.com", "https://peppol.domain.com/" + encodedParticipant + "/services/" + encodedBusdoxDocType},
                {"https://peppol.domain.com/", "https://peppol.domain.com/" + encodedParticipant + "/services/" + encodedBusdoxDocType},
                {"https://peppol.domain.com/smp", "https://peppol.domain.com/smp/" + encodedParticipant + "/services/" + encodedBusdoxDocType},
                {"https://peppol.domain.com/smp/", "https://peppol.domain.com/smp/" + encodedParticipant + "/services/" + encodedBusdoxDocType}
        };
    }
    @Test(dataProvider = "smpUrlsWithServices")
    public void testResolveServiceMetadata(String baseUrl, String expected) {
        URI location = URI.create(baseUrl);
        List<URI> uris = provider.resolveServiceMetadata(location, participant, busdoxDocumentType);

        Assert.assertFalse(uris.isEmpty(), "Expected non-empty resolved URI list");
        String actual = uris.get(0).toString();

        Assert.assertEquals(actual, expected, "Service metadata URI mismatch for location: " + baseUrl);
    }

    @DataProvider(name = "encodedParticipantAndBusDoxDocumentType")
    public Object[][] encodedParticipantAndBusDoxDocumentType() {
        String encodedParticipant = "iso6523-actorid-upis%3A%3A0204%3Aapi-global";
        String encodedDocumentType = "busdox-docid-qns%3A%3A" +
                "urn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AInvoice-2%3A%3AInvoice%23%23" +
                "urn%3Acen.eu%3Aen16931%3A2017%23compliant%23" +
                "urn%3Afdc%3Apeppol.eu%3A2017%3Apoacc%3Abilling%3A3.0%3A%3A2.1";

        return new Object[][]{
                {"https://peppol.domain.com", "https://peppol.domain.com/" + encodedParticipant + "/services/" + encodedDocumentType},
                {"https://peppol.domain.com/", "https://peppol.domain.com/" + encodedParticipant + "/services/" + encodedDocumentType},
                {"https://peppol.domain.com/smp", "https://peppol.domain.com/smp/" + encodedParticipant + "/services/" + encodedDocumentType},
                {"https://peppol.domain.com/smp/", "https://peppol.domain.com/smp/" + encodedParticipant + "/services/" + encodedDocumentType}
        };
    }
    @Test(dataProvider = "encodedParticipantAndBusDoxDocumentType")
    public void testResolveServiceMetadataWithEncodedParticipantAndBusDoxDocumentType(String baseUrl, String expectedUrl) {
        URI location = URI.create(baseUrl);
        List<URI> resolvedUris = provider.resolveServiceMetadata(location, participant, busdoxDocumentType);
        String actual = resolvedUris.get(0).toString();
        Assert.assertEquals(actual, expectedUrl, "Resolved service metadata URI is not as expected. Got: " + actual);
    }

    @DataProvider(name = "encodedParticipantAndPINTDocumentType")
    public Object[][] encodedParticipantAndPINTDocumentType() {
        String encodedParticipant = "iso6523-actorid-upis%3A%3A0204%3Aapi-global";
        String encodedPINTDocumentType = "peppol-doctype-wildcard%3A%3A" +
                "urn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AInvoice-2%3A%3AInvoice%23%23urn%3Apeppol%3Apint%3Abilling-1%40jp-1%3A%3A2.1";

        return new Object[][]{
                {"https://peppol.domain.com", "https://peppol.domain.com/" + encodedParticipant + "/services/" + encodedPINTDocumentType},
                {"https://peppol.domain.com/", "https://peppol.domain.com/" + encodedParticipant + "/services/" + encodedPINTDocumentType},
                {"https://peppol.domain.com/smp", "https://peppol.domain.com/smp/" + encodedParticipant + "/services/" + encodedPINTDocumentType},
                {"https://peppol.domain.com/smp/", "https://peppol.domain.com/smp/" + encodedParticipant + "/services/" + encodedPINTDocumentType}
        };
    }
    @Test(dataProvider = "encodedParticipantAndPINTDocumentType")
    public void testResolveServiceMetadataWithEncodedParticipantAndPINTDocumentType(String baseUrl, String expectedUrl) {
        URI location = URI.create(baseUrl);
        List<URI> resolvedUris = provider.resolveServiceMetadata(location, participant, pintWildCardDocumentType);
        String actual = resolvedUris.get(0).toString();
        Assert.assertEquals(actual, expectedUrl, "Resolved service metadata URI is not as expected. Got: " + actual);
    }

    @Test
    public void testNormalizeBasePathRemovesTrailingSlashes() {
        String normalized = invokeNormalize("https://domain.com/smp////");
        Assert.assertEquals(normalized, "https://domain.com/smp");
    }

    private String invokeNormalize(String url) {
        try {
            var method = DefaultProvider.class.getDeclaredMethod("normalizeBasePath", String.class);
            method.setAccessible(true);
            return (String) method.invoke(provider, url);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to invoke normalizeBasePath", e);
        }
    }

    @DataProvider(name = "wildcardUrls")
    public Object[][] wildcardUrls() {
        String encodedParticipant = "iso6523-actorid-upis%3A%3A0204%3Aapi-global";
        String encodedDocType1 = "urn%3Apeppol%3Apint%3Abilling-1%40jp-1%40domestic%2A%3A%3A2.1";

        return new Object[][]{
                {"https://peppol.domain.com", encodedParticipant, encodedDocType1},
                {"https://peppol.domain.com/smp/", encodedParticipant, encodedDocType1}
        };
    }
    @Test(dataProvider = "wildcardUrls")
    public void testPintWildcardUris(String baseUrl, String encodedParticipant, String encodedDocTypePart) {
        URI location = URI.create(baseUrl);

        List<URI> resolvedUris = provider.resolveServiceMetadata(location, participant, pintWildCardDocumentType);
        Assert.assertFalse(resolvedUris.isEmpty(), "Expected at least one wildcard URI to be resolved");
        String actual = resolvedUris.get(0).toString();

        Assert.assertTrue(actual.startsWith(baseUrl.replaceAll("/+$", "")), "Wildcard URI should start with base SMP URL. Got: " + actual);
        Assert.assertTrue(actual.contains(encodedParticipant + "/services/"),"Wildcard URI should contain participant/services structure. Got: " + actual);
        Assert.assertTrue(actual.contains("pint%3Abilling-1"),"Wildcard URI should include PINT customization pattern. Got: " + actual);
    }
}