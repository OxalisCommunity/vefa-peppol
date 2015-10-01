package no.difi.vefa.edelivery.lookup.api;

import java.security.cert.X509Certificate;

public interface CertificateValidator {
    void validate(X509Certificate certificate) throws SecurityException;
}
