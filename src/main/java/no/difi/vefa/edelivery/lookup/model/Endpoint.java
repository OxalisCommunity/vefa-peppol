package no.difi.vefa.edelivery.lookup.model;

import java.io.Serializable;
import java.security.cert.X509Certificate;

public class Endpoint implements Serializable {

    private static final long serialVersionUID = 5892469135654700883L;

    private ProcessIdentifier processIdentifier;
    private TransportProfile transportProfile;
    private String address;
    private X509Certificate certificate;

    public Endpoint(ProcessIdentifier processIdentifier, TransportProfile transportProfile, String address, X509Certificate certificate) {
        this.processIdentifier = processIdentifier;
        this.transportProfile = transportProfile;
        this.address = address;
        this.certificate = certificate;
    }

    public ProcessIdentifier getProcessIdentifier() {
        return processIdentifier;
    }

    public TransportProfile getTransportProfile() {
        return transportProfile;
    }

    public String getAddress() {
        return address;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }
}
