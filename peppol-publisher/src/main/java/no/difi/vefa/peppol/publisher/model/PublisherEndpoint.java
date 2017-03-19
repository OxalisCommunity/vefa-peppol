package no.difi.vefa.peppol.publisher.model;

import no.difi.vefa.peppol.common.SimpleEndpoint;
import no.difi.vefa.peppol.common.model.TransportProfile;

import java.net.URI;
import java.util.Date;

/**
 * @author erlend
 */
public class PublisherEndpoint implements SimpleEndpoint {

    private TransportProfile transportProfile;

    private URI address;

    private byte[] certificate;

    private Date activationDate;

    private Date expirationDate;

    private String description;

    private String technicalContact;

    public PublisherEndpoint(TransportProfile transportProfile, URI address, byte[] certificate, Date activationDate,
                             Date expirationDate, String description, String technicalContact) {
        this.transportProfile = transportProfile;
        this.address = address;
        this.certificate = certificate;
        this.activationDate = activationDate;
        this.expirationDate = expirationDate;
        this.description = description;
        this.technicalContact = technicalContact;
    }

    @Override
    public TransportProfile getTransportProfile() {
        return transportProfile;
    }

    public URI getAddress() {
        return address;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getDescription() {
        return description;
    }

    public String getTechnicalContact() {
        return technicalContact;
    }
}
