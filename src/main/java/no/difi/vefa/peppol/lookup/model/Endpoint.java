package no.difi.vefa.peppol.lookup.model;

import java.io.Serializable;
import java.security.cert.X509Certificate;

public class Endpoint implements Serializable {

    private static final long serialVersionUID = 5892469135654700883L;

    private String transportProfile;
    private String address;
    private X509Certificate certificate;

    public Endpoint(String transportProfile, String address, X509Certificate certificate) {
        this.transportProfile = transportProfile;
        this.address = address;
        this.certificate = certificate;
    }

    public String getTransportProfile() {
        return transportProfile;
    }

    public String getAddress() {
        return address;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }
}
