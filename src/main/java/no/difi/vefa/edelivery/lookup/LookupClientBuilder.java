package no.difi.vefa.edelivery.lookup;

import no.difi.vefa.edelivery.lookup.api.MetadataFetcher;
import no.difi.vefa.edelivery.lookup.api.MetadataLocator;
import no.difi.vefa.edelivery.lookup.api.MetadataProvider;
import no.difi.vefa.edelivery.lookup.api.MetadataReader;
import no.difi.vefa.edelivery.lookup.fetcher.UrlFetcher;
import no.difi.vefa.edelivery.lookup.locator.BusdoxLocator;
import no.difi.vefa.edelivery.lookup.provider.DefaultProvider;
import no.difi.vefa.edelivery.lookup.reader.MultiReader;

public class LookupClientBuilder {

    public static LookupClientBuilder newInstance() {
        return new LookupClientBuilder();
    }

    public static LookupClientBuilder forProduction() {
        return newInstance()
                .locator(new BusdoxLocator(BusdoxLocator.OPENPEPPOL_PRODUCTION));
    }

    public static LookupClientBuilder forTest() {
        return newInstance()
                .locator(new BusdoxLocator(BusdoxLocator.OPENPEPPOL_TEST));
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

        // Defaults
        if (metadataProvider == null)
            provider(new DefaultProvider());
        if (metadataFetcher == null)
            fetcher(new UrlFetcher());
        if (metadataReader == null)
            reader(new MultiReader());

        return new LookupClient(metadataLocator, metadataProvider, metadataFetcher, metadataReader);
    }

}
