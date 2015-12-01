package no.difi.vefa.peppol.security.model;

import java.security.cert.X509Certificate;

public class EndpointCertificate {

    private X509Certificate certificate;

    public EndpointCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }
}
