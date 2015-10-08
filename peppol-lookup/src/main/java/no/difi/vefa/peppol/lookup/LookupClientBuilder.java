package no.difi.vefa.peppol.lookup;

import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.lookup.api.MetadataFetcher;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import no.difi.vefa.peppol.lookup.api.MetadataProvider;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
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
    private CertificateValidator providerCertificateValidator;
    private MetadataProvider metadataProvider;
    private MetadataReader metadataReader;
    private CertificateValidator endpointertificateValidator;

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

    public LookupClientBuilder providerCertificateValidator(CertificateValidator certificateValidator) {
        this.providerCertificateValidator = certificateValidator;
        return this;
    }

    public LookupClientBuilder reader(MetadataReader metadataReader) {
        this.metadataReader = metadataReader;
        return this;
    }

    public LookupClientBuilder endpointCertificateValidator(CertificateValidator certificateValidator) {
        this.endpointertificateValidator = certificateValidator;
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

        return new LookupClient(metadataLocator, metadataProvider, metadataFetcher, metadataReader, providerCertificateValidator, endpointertificateValidator);
    }

}
