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

package network.oxalis.vefa.peppol.security.util;

import com.typesafe.config.ConfigFactory;
import network.oxalis.commons.certvalidator.Validator;
import network.oxalis.commons.certvalidator.api.CertificateValidationException;
import network.oxalis.vefa.peppol.common.code.Service;
import network.oxalis.vefa.peppol.common.lang.PeppolLoadingException;
import network.oxalis.vefa.peppol.mode.Mode;
import network.oxalis.vefa.peppol.security.api.CertificateValidator;
import network.oxalis.vefa.peppol.security.lang.PeppolSecurityException;
import org.testng.annotations.Test;

public class DifiCertificateValidatorTest {

    @Test(expectedExceptions = PeppolLoadingException.class)
    public void loadingException() throws PeppolLoadingException {
        // Create invalid configuration and mode.
        Mode mode = Mode.of(ConfigFactory.parseString("security.pki = /testing.txt"), null);

        // Initiate validator without the required configuration.
        mode.initiate(DifiCertificateValidator.class);
    }

    @Test(enabled = false)
    public void simpleApProd() throws PeppolLoadingException, CertificateValidationException, PeppolSecurityException {
        CertificateValidator validator = Mode.of(Mode.PRODUCTION).initiate(DifiCertificateValidator.class);
        validator.validate(Service.AP, Validator.getCertificate(getClass().getResourceAsStream("/ap-prod.cer")));
    }

    @Test
    public void simpleApTest() throws PeppolLoadingException, CertificateValidationException, PeppolSecurityException {
        CertificateValidator validator = Mode.of(Mode.TEST).initiate(DifiCertificateValidator.class);
        validator.validate(Service.AP, Validator.getCertificate(getClass().getResourceAsStream("/ap-test.cer")));
    }

    @Test
    public void simpleSmpProd() throws PeppolLoadingException, CertificateValidationException, PeppolSecurityException {
        CertificateValidator validator = Mode.of(Mode.PRODUCTION).initiate(DifiCertificateValidator.class);
        validator.validate(Service.SMP, Validator.getCertificate(getClass().getResourceAsStream("/smp-random-prod.cer")));
    }

    @Test
    public void simpleSmpTest() throws PeppolLoadingException, CertificateValidationException, PeppolSecurityException {
        CertificateValidator validator = Mode.of(Mode.TEST).initiate(DifiCertificateValidator.class);
        validator.validate(Service.SMP, Validator.getCertificate(getClass().getResourceAsStream("/smp-random-test.cer")));
    }
}
