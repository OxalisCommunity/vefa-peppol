package no.difi.vefa.peppol.lookup;

import no.difi.vefa.peppol.lookup.api.MetadataFetcher;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import no.difi.vefa.peppol.lookup.api.MetadataProvider;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.lookup.fetcher.ApacheFetcher;
import no.difi.vefa.peppol.lookup.fetcher.UrlFetcher;
import no.difi.vefa.peppol.lookup.locator.BusdoxLocator;
import no.difi.vefa.peppol.lookup.provider.DefaultProvider;
import no.difi.vefa.peppol.lookup.reader.MultiReader;

public class LookupClientBuilder {

    public static LookupClientBuilder newInstance() {
        return new LookupClientBuilder();
    }

    public static LookupClientBuilder forProduction() {
        return newInstance()
                .locator(new BusdoxLocator(BusdoxLocator.OPENPEPPOL_PRODUCTION))
                .provider(new DefaultProvider())
                .fetcher(new UrlFetcher())
                .reader(new MultiReader());
    }

    public static LookupClientBuilder forTest() {
        return newInstance()
                .locator(new BusdoxLocator(BusdoxLocator.OPENPEPPOL_TEST))
                .provider(new DefaultProvider())
                .fetcher(new UrlFetcher())
                .reader(new MultiReader());
    }

    LookupClientBuilder() {
        // No action
    }

    private MetadataFetcher metadataFetcher;
    private MetadataLocator metadataLocator;
    private MetadataProvider metadataProvider;
    private MetadataReader metadataReader;

    public LookupClientBuilder fetcher(MetadataFetcher metadataFetcher) {
        this.metadataFetcher = metadataFetcher;
        return this;
    }

    public LookupClientBuilder locator(MetadataLocator metadataLocator) {
        this.metadataLocator = metadataLocator;
        return this;
    }

    public LookupClientBuilder provider(MetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
        return this;
    }

    public LookupClientBuilder reader(MetadataReader metadataReader) {
        this.metadataReader = metadataReader;
        return this;
    }

    public LookupClient build() {
        if (metadataLocator == null)
            throw new IllegalStateException("Locator not defined.");
        if (metadataProvider == null)
            throw new IllegalStateException("Provider not defined.");
        if (metadataFetcher == null)
            throw new IllegalStateException("Fetcher not defined.");
        if (metadataReader == null)
            throw new IllegalStateException("Reader not defined.");

        return new LookupClient(metadataLocator, metadataProvider, metadataFetcher, metadataReader);
    }

}
