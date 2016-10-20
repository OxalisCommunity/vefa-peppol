/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
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

package no.difi.vefa.peppol.security.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;

import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class CachedCertificateValidator
        extends CacheLoader<X509Certificate, CachedCertificateValidator.Result>
        implements CertificateValidator {

    private CertificateValidator certificateValidator;

    private LoadingCache<X509Certificate, Result> cache;

    public CachedCertificateValidator(CertificateValidator certificateValidator, long timeout) {
        this.certificateValidator = certificateValidator;

        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(timeout, TimeUnit.SECONDS)
                .build(this);
    }

    @Override
    public void validate(X509Certificate certificate) throws PeppolSecurityException {
        cache.getUnchecked(certificate).trigger();
    }

    @Override
    public Result load(X509Certificate certificate) throws Exception {
        try {
            certificateValidator.validate(certificate);
            return new Result();
        } catch (Exception e) {
            return new Result(e);
        }
    }

    protected class Result {

        private PeppolSecurityException exception;

        public Result() {
            // No action.
        }

        public Result(Exception e) {
            if (e instanceof PeppolSecurityException)
                this.exception = (PeppolSecurityException) e;
            else
                this.exception = new PeppolSecurityException(e.getMessage(), e);
        }

        public void trigger() throws PeppolSecurityException {
            if (exception != null)
                throw exception;
        }
    }
}
