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

import no.difi.vefa.peppol.common.lang.PeppolLoadingException;
import no.difi.vefa.peppol.lookup.api.MetadataFetcher;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import no.difi.vefa.peppol.lookup.api.MetadataProvider;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.mode.Mode;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.util.EmptyCertificateValidator;

public class LookupClientBuilder {

    private Mode mode;

    private MetadataFetcher metadataFetcher;

    private MetadataLocator metadataLocator;

    private CertificateValidator certificateValidator = EmptyCertificateValidator.INSTANCE;

    private MetadataProvider metadataProvider;

    private MetadataReader metadataReader;

    public static LookupClientBuilder newInstance(Mode mode) {
        return new LookupClientBuilder(mode);
    }

    public static LookupClientBuilder forMode(Mode mode) throws PeppolLoadingException {
        return newInstance(mode)
                .certificateValidator(mode.initiate("security.validator.class", CertificateValidator.class));
    }

    public static LookupClientBuilder forMode(String modeIdentifier) throws PeppolLoadingException {
        return forMode(Mode.of(modeIdentifier));
    }

    public static LookupClientBuilder forProduction() throws PeppolLoadingException {
        return forMode("PRODUCTION");
    }

    public static LookupClientBuilder forTest() throws PeppolLoadingException {
        return forMode("TEST");
    }

    LookupClientBuilder(Mode mode) {
        this.mode = mode;
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

    public LookupClientBuilder certificateValidator(CertificateValidator certificateValidator) {
        this.certificateValidator = certificateValidator;
        return this;
    }

    public LookupClient build() throws PeppolLoadingException {
        if (metadataLocator == null)
            locator(mode.initiate("locator.class", MetadataLocator.class));
        if (metadataProvider == null)
            provider(mode.initiate("provider.class", MetadataProvider.class));
        if (metadataFetcher == null)
            fetcher(mode.initiate("fetcher.class", MetadataFetcher.class));
        if (metadataReader == null)
            reader(mode.initiate("reader.class", MetadataReader.class));

        return new LookupClient(metadataLocator, metadataProvider, metadataFetcher, metadataReader, certificateValidator);
    }
}
