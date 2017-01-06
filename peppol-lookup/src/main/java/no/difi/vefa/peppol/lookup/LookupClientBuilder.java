/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

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
import no.difi.vefa.peppol.security.util.EmptyCertificateValidator;

public class LookupClientBuilder {

    private MetadataFetcher metadataFetcher;

    private MetadataLocator metadataLocator;

    private CertificateValidator providerCertificateValidator = EmptyCertificateValidator.INSTANCE;

    private MetadataProvider metadataProvider;

    private MetadataReader metadataReader;

    private CertificateValidator endpointertificateValidator = EmptyCertificateValidator.INSTANCE;

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

    public LookupClientBuilder reader(MetadataReader metadataReader) {
        this.metadataReader = metadataReader;
        return this;
    }

    public LookupClientBuilder providerCertificateValidator(CertificateValidator certificateValidator) {
        this.providerCertificateValidator = certificateValidator;
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
