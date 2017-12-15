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

package no.difi.vefa.peppol.security;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.common.lang.PeppolLoadingException;
import no.difi.vefa.peppol.mode.Mode;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;

public class ModeDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModeDetector.class);

    public static Mode detect(X509Certificate certificate) throws PeppolLoadingException {
        return detect(certificate, ConfigFactory.load());
    }

    public static Mode detect(X509Certificate certificate, Config config) throws PeppolLoadingException {
        for (String token : config.getObject("mode").keySet()) {
            if (!"default".equals(token)) {
                try {
                    Mode mode = Mode.of(config, token);
                    mode.initiate("security.validator.class", CertificateValidator.class)
                            .validate(Service.ALL, certificate);
                    return mode;
                } catch (PeppolSecurityException e) {
                    LOGGER.info("Detection error ({}): {}", token, e.getMessage());
                }
            }
        }

        throw new PeppolLoadingException(
                String.format("Unable to detect mode for certificate '%s'.", certificate.getSubjectDN().toString()));
    }
}
