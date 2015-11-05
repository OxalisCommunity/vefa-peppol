package no.difi.vefa.peppol.lookup;

import no.difi.vefa.peppol.common.Mode;
import no.difi.vefa.peppol.lookup.api.MetadataFetcher;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import no.difi.vefa.peppol.lookup.api.MetadataProvider;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.lookup.fetcher.UrlFetcher;
import no.difi.vefa.peppol.lookup.locator.DynamicLocator;
import no.difi.vefa.peppol.lookup.provider.DefaultProvider;
import no.difi.vefa.peppol.lookup.reader.MultiReader;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.context.PeppolContext;
import no.difi.vefa.peppol.security.util.DifiCertificateValidator;

public class LookupClientBuilder {

    public static LookupClientBuilder newInstance() {
        return new LookupClientBuilder();
    }

    public static LookupClientBuilder forMode(Mode mode) {
        switch (mode) {
            case PRODUCTION:
                return forProduction();
            case TEST:
                return forTest();
            default:
                return newInstance();
        }
    }

    public static LookupClientBuilder forProduction() {
        PeppolContext peppolContext = new PeppolContext("production");
        return newInstance()
                .locator(new DynamicLocator(DynamicLocator.OPENPEPPOL_PRODUCTION))
                .endpointCertificateValidator(new DifiCertificateValidator(peppolContext.apValidator()))
                .providerCertificateValidator(new DifiCertificateValidator(peppolContext.smpValidator()));
    }

    public static LookupClientBuilder forTest() {
        PeppolContext peppolContext = new PeppolContext("test");
        return newInstance()
                .locator(new DynamicLocator(DynamicLocator.OPENPEPPOL_TEST))
                .endpointCertificateValidator(new DifiCertificateValidator(peppolContext.apValidator()))
                .providerCertificateValidator(new DifiCertificateValidator(peppolContext.smpValidator()));
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