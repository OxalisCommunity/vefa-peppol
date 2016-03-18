package no.difi.vefa.peppol.security;

import no.difi.certvalidator.Validator;
import no.difi.vefa.peppol.security.api.PeppolSecurityException;
import org.testng.Assert;
import org.testng.annotations.Test;

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
}
