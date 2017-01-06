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

package no.difi.vefa.peppol.security;

import no.difi.certvalidator.Validator;
import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.security.api.ModeDescription;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.security.cert.X509Certificate;

public class ModeTest {

    @Test
    public void simple() throws Exception {
        X509Certificate certificate = Validator.getCertificate(getClass().getResourceAsStream("/ap-difi-prod.cer"));
        Assert.assertEquals("PRODUCTION", Mode.detect(certificate));

        certificate = Validator.getCertificate(getClass().getResourceAsStream("/ap-difi-test.cer"));
        Assert.assertEquals("TEST", Mode.detect(certificate));
    }

    @Test(expectedExceptions = PeppolSecurityException.class)
    public void simpleDetectNull() throws Exception {
        X509Certificate certificate = Validator.getCertificate(getClass().getResourceAsStream("/web-difi.cer"));
        Assert.assertEquals("PRODUCTION", Mode.detect(certificate));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void triggerExceptionWhenModeIsNotFound() throws Exception {
        Mode.valueOf("Invalid!");
    }

    @Test
    public void simpleLoading() throws Exception {
        Mode mode = Mode.valueOf("PRODUCTION");
        Assert.assertNotNull(mode);
        Assert.assertNotNull(mode.getKeyStoreBucket());
    }

    @Test
    public void verifyModeIsNotOverriddenByAccident() throws Exception {
        Mode.add(new ModeDescription() {
            @Override
            public String getIdentifier() {
                return Mode.PRODUCTION;
            }

            @Override
            public String[] getIssuers(Service service) {
                return new String[0];
            }

            @Override
            public InputStream getKeystore() {
                return null;
            }
        });

        Mode mode = Mode.valueOf("PRODUCTION");
        Assert.assertNotNull(mode);
        Assert.assertNotNull(mode.getKeyStoreBucket());
    }

    @Test
    public void exceptionOnErrorLoading() throws Exception {
        Mode.add(new ModeDescription() {
            @Override
            public String getIdentifier() {
                return "UNITTEST";
            }

            @Override
            public String[] getIssuers(Service service) {
                return new String[0];
            }

            @Override
            public InputStream getKeystore() {
                return null;
            }
        });

        try {
            Mode.valueOf("UNITTEST");
            Assert.fail("Should throw exception.");
        } catch (RuntimeException e) {

        }

        Mode.remove("UNITTEST");
    }
}
