package no.difi.vefa.peppol.common.model;

import java.io.Serializable;
import java.security.cert.X509Certificate;

public class Endpoint implements Serializable {

    private static final long serialVersionUID = 5892469135654700883L;

    private ProcessIdentifier processIdentifier;

    private TransportProfile transportProfile;

    private String address;

    private X509Certificate certificate;

    public Endpoint(ProcessIdentifier processIdentifier, TransportProfile transportProfile, String address,
                    X509Certificate certificate) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Endpoint endpoint = (Endpoint) o;

        if (!processIdentifier.equals(endpoint.processIdentifier)) return false;
        if (!transportProfile.equals(endpoint.transportProfile)) return false;
        if (!address.equals(endpoint.address)) return false;
        return certificate.equals(endpoint.certificate);

    }

    @Override
    public int hashCode() {
        int result = processIdentifier.hashCode();
        result = 31 * result + transportProfile.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + certificate.hashCode();
        return result;
    }
}
