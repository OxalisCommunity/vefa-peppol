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
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.common.lang.PeppolLoadingException;
import no.difi.vefa.peppol.mode.Mode;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;

import java.security.cert.X509Certificate;
import java.util.Map;

@Slf4j
public class ModeDetector {

    public static Mode detect(X509Certificate certificate) throws PeppolLoadingException {
        return detect(certificate, ConfigFactory.load(), null);
    }

    public static Mode detect(X509Certificate certificate, Config config, Map<String, Object> objectStorage) throws PeppolLoadingException {
        for (String token : config.getObject("mode").keySet()) {
            if (!"default".equals(token)) {
                try {
                    Mode mode = Mode.of(config, token);
                    mode.initiate("security.validator.class", CertificateValidator.class, objectStorage)
                            .validate(Service.ALL, certificate);

                    log.info("Detected mode: {}", mode.getIdentifier());
                    if (mode.hasString("security.message"))
                        log.info(mode.getString("security.message"));

                    return mode;
                } catch (PeppolSecurityException e) {
                    log.info("Detection error ({}): {}", token, e.getMessage());
                }
            }
        }

        throw new PeppolLoadingException(
                String.format("Unable to detect mode for certificate '%s'.", certificate.getSubjectDN().toString()));
    }
}
