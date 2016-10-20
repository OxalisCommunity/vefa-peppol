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

import com.google.common.io.ByteStreams;
import no.difi.certvalidator.Validator;
import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.security.Mode;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;

public class CachedCertificateValidatorTest {

    @Test
    public void simple() throws Exception {
        Mode mode = Mode.valueOf(Mode.PRODUCTION);

        CertificateValidator originalValidator = mode.validator(Service.AP);
        CountingCertificateValidator countingValidator = new CountingCertificateValidator(originalValidator);
        CertificateValidator validator = new CachedCertificateValidator(countingValidator, 60);

        byte[] cert = ByteStreams.toByteArray(getClass().getResourceAsStream("/ap-difi-prod.cer"));

        validator.validate(Validator.getCertificate(cert));
        validator.validate(Validator.getCertificate(cert));
        validator.validate(Validator.getCertificate(cert));
        validator.validate(Validator.getCertificate(cert));
        validator.validate(Validator.getCertificate(cert));

        Assert.assertEquals(countingValidator.counter, 1);
    }

    public class CountingCertificateValidator implements CertificateValidator {
        public long counter = 0;

        private CertificateValidator certificateValidator;

        public CountingCertificateValidator(CertificateValidator certificateValidator) {
            this.certificateValidator = certificateValidator;
        }

        @Override
        public void validate(X509Certificate certificate) throws PeppolSecurityException {
            counter++;
            certificateValidator.validate(certificate);
        }
    }
}
