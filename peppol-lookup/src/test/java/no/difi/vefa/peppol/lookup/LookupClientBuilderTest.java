package no.difi.vefa.peppol.lookup;

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public class LookupClientBuilderTest {

    @Test
    public void success() {
        assertNotNull(LookupClientBuilder.forProduction());
        assertNotNull(LookupClientBuilder.forTest());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingLocator() {
        LookupClientBuilder.forProduction().locator(null).build();
        fail("Exception expected");
    }

    @Test
    public void testMissingProvider() {
        assertNotNull(LookupClientBuilder.forProduction().provider(null).build());
    }

    @Test
    public void testMissingFetcher() {
        assertNotNull(LookupClientBuilder.forProduction().fetcher(null).build());
    }

    @Test
    public void testMissingReader() {
        assertNotNull(LookupClientBuilder.forProduction().reader(null).build());
    }
}
