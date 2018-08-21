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

package no.difi.vefa.peppol.security.util;

import no.difi.certvalidator.ValidatorGroup;
import no.difi.certvalidator.ValidatorLoader;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.certvalidator.lang.ValidatorParsingException;
import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.common.lang.PeppolLoadingException;
import no.difi.vefa.peppol.mode.Mode;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Map;

public class DifiCertificateValidator implements CertificateValidator {

    private ValidatorGroup validator;

    private Mode mode;

    public DifiCertificateValidator(Mode mode, Map<String, Object> objectStorage) throws PeppolLoadingException {
        this.mode = mode;

        try (InputStream inputStream = getClass().getResourceAsStream(mode.getString("security.pki"))) {
            validator = ValidatorLoader.newInstance()
                    .putAll(objectStorage)
                    .build(inputStream);
        } catch (ValidatorParsingException | IOException e) {
            throw new PeppolLoadingException("Unable to initiate PKI.", e);
        }
    }

    public DifiCertificateValidator(Mode mode) throws PeppolLoadingException {
        this(mode, null);
    }

    @Override
    public void validate(Service service, X509Certificate certificate) throws PeppolSecurityException {
        try {
            validator.validate(mode.getString(String.format("security.validator.%s", service.toString())), certificate);
        } catch (CertificateValidationException e) {
            throw new PeppolSecurityException(e.getMessage(), e);
        }
    }
}
