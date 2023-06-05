/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package network.oxalis.vefa.peppol.lookup;

import java.util.HashMap;
import java.util.Map;

import network.oxalis.vefa.peppol.common.lang.PeppolLoadingException;
import network.oxalis.vefa.peppol.lookup.api.MetadataFetcher;
import network.oxalis.vefa.peppol.lookup.api.MetadataLocator;
import network.oxalis.vefa.peppol.lookup.api.MetadataProvider;
import network.oxalis.vefa.peppol.lookup.api.MetadataReader;
import network.oxalis.vefa.peppol.mode.Mode;
import network.oxalis.vefa.peppol.security.api.CertificateValidator;
import network.oxalis.vefa.peppol.security.util.EmptyCertificateValidator;

public class LookupClientBuilder {

    private final Mode mode;

    protected MetadataFetcher metadataFetcher;

    protected MetadataLocator metadataLocator;

    protected CertificateValidator certificateValidator = EmptyCertificateValidator.INSTANCE;

    protected MetadataProvider metadataProvider;

    protected MetadataReader metadataReader;

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
        return forMode(Mode.PRODUCTION);
    }

    public static LookupClientBuilder forTest() throws PeppolLoadingException {
        return forMode(Mode.TEST);
    }

    private LookupClientBuilder(Mode mode) {
        this.mode = mode;
    }

    public LookupClientBuilder fetcher(MetadataFetcher metadataFetcher) {
        this.metadataFetcher = metadataFetcher;
        return this;
    }

    public LookupClientBuilder fetcher(Class<? extends MetadataFetcher> metadataFetcher) throws PeppolLoadingException {
        return fetcher(mode.initiate(metadataFetcher));
    }

    public LookupClientBuilder locator(MetadataLocator metadataLocator) {
        this.metadataLocator = metadataLocator;
        return this;
    }

    public LookupClientBuilder locator(Class<? extends MetadataLocator> metadataLocator) throws PeppolLoadingException {
        return locator(mode.initiate(metadataLocator));
    }

    public LookupClientBuilder provider(MetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
        return this;
    }

    public LookupClientBuilder provider(Class<? extends MetadataProvider> metadataProvider)
            throws PeppolLoadingException {
        return provider(mode.initiate(metadataProvider));
    }

    public LookupClientBuilder reader(MetadataReader metadataReader) {
        this.metadataReader = metadataReader;
        return this;
    }

    public LookupClientBuilder reader(Class<? extends MetadataReader> metadataReader) throws PeppolLoadingException {
        return reader(mode.initiate(metadataReader));
    }

    public LookupClientBuilder certificateValidator(CertificateValidator certificateValidator) {
        this.certificateValidator = certificateValidator;
        return this;
    }

    public LookupClient build() throws PeppolLoadingException {
        if (metadataLocator == null)
            locator(mode.initiate("lookup.locator.class", MetadataLocator.class));
        if (metadataProvider == null)
            provider(mode.initiate("lookup.provider.class", MetadataProvider.class));
        if (metadataFetcher == null)
            fetcher(mode.initiate("lookup.fetcher.class", MetadataFetcher.class));
        if (metadataReader == null)
            reader(mode.initiate("lookup.reader.class", MetadataReader.class));
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(LookupClientBuilder.class.getName(), this);
        return mode.initiate("lookup.impl.class", null, map); 
    }
}
