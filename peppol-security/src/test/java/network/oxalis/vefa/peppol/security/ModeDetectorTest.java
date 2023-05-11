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

package network.oxalis.vefa.peppol.security;

import network.oxalis.commons.certvalidator.Validator;
import network.oxalis.vefa.peppol.common.lang.PeppolLoadingException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;

public class ModeDetectorTest {

    @Test
    public void simpleConstructor() {
        new ModeDetector();
    }

    @Test(expectedExceptions = PeppolLoadingException.class)
    public void simpleDetectError() throws Exception {
        X509Certificate certificate = Validator.getCertificate(getClass().getResourceAsStream("/web-difi.cer"));
        ModeDetector.detect(certificate).getString("security.pki");
    }

    @Test(enabled = false)
    public void simpleDetectProduction() throws Exception {
        X509Certificate certificate = Validator.getCertificate(getClass().getResourceAsStream("/ap-prod.cer"));
        Assert.assertEquals(ModeDetector.detect(certificate).getString("security.pki"), "/pki/peppol-production.xml");
    }

    @Test
    public void simpleDetectTest() throws Exception {
        X509Certificate certificate = Validator.getCertificate(getClass().getResourceAsStream("/ap-test.cer"));
        Assert.assertEquals(ModeDetector.detect(certificate).getString("security.pki"), "/pki/peppol-test.xml");
    }

    @Test
    public void simpleDetectFRTest() throws Exception {
        X509Certificate certificate = Validator.getCertificate(getClass().getResourceAsStream("/fr-poc-test.cer"));
        Assert.assertEquals(ModeDetector.detect(certificate).getString("security.pki"), "/pki/fr-poc-test.xml");
    }

    @Test
    public void simpleDetectLocal() throws Exception {
        X509Certificate certificate = Validator.getCertificate(getClass().getResourceAsStream("/self-signed.cer"));
        Assert.assertEquals(ModeDetector.detect(certificate).getString("security.pki"), "/pki/local.xml");
    }
}
