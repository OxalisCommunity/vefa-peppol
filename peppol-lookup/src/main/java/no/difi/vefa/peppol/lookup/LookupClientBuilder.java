package no.difi.vefa.peppol.lookup;

import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.lookup.api.MetadataFetcher;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import no.difi.vefa.peppol.lookup.api.MetadataProvider;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.lookup.fetcher.UrlFetcher;
import no.difi.vefa.peppol.lookup.locator.DynamicLocator;
import no.difi.vefa.peppol.lookup.provider.DefaultProvider;
import no.difi.vefa.peppol.lookup.reader.MultiReader;
import no.difi.vefa.peppol.security.Mode;
import no.difi.vefa.peppol.security.api.CertificateValidator;

public class LookupClientBuilder {

    public static LookupClientBuilder newInstance() {
        return new LookupClientBuilder();
    }

    public static LookupClientBuilder forMode(String modeIdentifier) {
        Mode mode = Mode.valueOf(modeIdentifier);
        String locator;

        switch (modeIdentifier) {
            case "PRODUCTION":
                locator = DynamicLocator.OPENPEPPOL_PRODUCTION;
                break;
            case "TEST":
                locator = DynamicLocator.OPENPEPPOL_TEST;
                break;
            default:
                return newInstance();
        }

        return newInstance()
                .locator(new DynamicLocator(locator))
                .endpointCertificateValidator(mode.validator(Service.AP))
                .providerCertificateValidator(mode.validator(Service.SMP));
    }

    public static LookupClientBuilder forProduction() {
        return forMode("PRODUCTION");
    }

    public static LookupClientBuilder forTest() {
        return forMode("TEST");
    }

    private MetadataFetcher metadataFetcher;

    private MetadataLocator metadataLocator;

    private CertificateValidator providerCertificateValidator;

    private MetadataProvider metadataProvider;

    private MetadataReader metadataReader;

    private CertificateValidator endpointertificateValidator;

    LookupClientBuilder() {
        // No action
    }

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

        return new LookupClient(metadataLocator, metadataProvider, metadataFetcher, metadataReader,
                providerCertificateValidator, endpointertificateValidator);
    }
}
