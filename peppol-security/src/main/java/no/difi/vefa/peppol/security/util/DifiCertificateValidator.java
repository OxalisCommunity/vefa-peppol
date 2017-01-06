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

package no.difi.vefa.peppol.security.util;

import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.vefa.peppol.security.api.*;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;

public class DifiCertificateValidator implements CertificateValidator {

    private static Logger logger = LoggerFactory.getLogger(DifiCertificateValidator.class);

    public Validator validator;

    public DifiCertificateValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(X509Certificate certificate) throws PeppolSecurityException {
        try {
            logger.debug("Validating '{}'.", certificate.getSubjectX500Principal().getName());

            validator.validate(certificate);
        } catch (CertificateValidationException e) {
            throw new PeppolSecurityException(e.getMessage(), e);
        }
    }
}
