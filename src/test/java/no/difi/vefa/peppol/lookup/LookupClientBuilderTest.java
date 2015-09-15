package no.difi.vefa.peppol.lookup;

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public class LookupClientBuilderTest {

    @Test
    public void success() {
        assertNotNull(LookupClientBuilder.forProduction().build());
        assertNotNull(LookupClientBuilder.forTest().build());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingLocator() {
        LookupClientBuilder.forProduction().locator(null).build();
        fail("Exception expected");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingProvider() {
        LookupClientBuilder.forProduction().provider(null).build();
        fail("Exception expected");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingFetcher() {
        LookupClientBuilder.forProduction().fetcher(null).build();
        fail("Exception expected");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingReader() {
        LookupClientBuilder.forProduction().reader(null).build();
        fail("Exception expected");
    }
}
