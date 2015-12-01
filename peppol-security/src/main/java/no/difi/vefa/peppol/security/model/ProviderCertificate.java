package no.difi.vefa.peppol.security.model;

import java.security.cert.X509Certificate;

public class ProviderCertificate {

    private X509Certificate certificate;

    public ProviderCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }
}
