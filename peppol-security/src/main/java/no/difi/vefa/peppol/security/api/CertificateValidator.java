package no.difi.vefa.peppol.security.api;

import java.security.cert.X509Certificate;

public interface CertificateValidator {

    void validate(X509Certificate certificate) throws PeppolSecurityException;
    
}
