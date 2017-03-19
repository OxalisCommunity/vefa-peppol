package no.difi.vefa.peppol.publisher.builder;

import no.difi.vefa.peppol.common.model.TransportProfile;
import no.difi.vefa.peppol.publisher.model.PublisherEndpoint;

import java.net.URI;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author erlend
 */
public class EndpointBuilder {

    private TransportProfile transportProfile;

    private URI address;

    private byte[] certificate;

    private Date activationDate;

    private Date expirationDate;

    private String description;

    private String technicalContact;

    public static EndpointBuilder newInstance() {
        return new EndpointBuilder();
    }

    public EndpointBuilder transportProfile(TransportProfile transportProfile) {
        this.transportProfile = transportProfile;
        return this;
    }

    public EndpointBuilder address(URI address) {
        this.address = address;
        return this;
    }

    public EndpointBuilder certificate(byte[] certificate) {
        this.certificate = certificate;
        return this;
    }

    public EndpointBuilder certificate(X509Certificate certificate) throws CertificateEncodingException {
        return certificate(certificate.getEncoded())
                .activationDate(certificate.getNotBefore())
                .expirationDate(certificate.getNotAfter());
    }

    public EndpointBuilder activationDate(Date activationDate) {
        this.activationDate = activationDate;
        return this;
    }

    public EndpointBuilder expirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public EndpointBuilder description(String description) {
        this.description = description;
        return this;
    }

    public EndpointBuilder technicalContact(String technicalContact) {
        this.technicalContact = technicalContact;
        return this;
    }

    public PublisherEndpoint build() {
        return new PublisherEndpoint(transportProfile, address, certificate, activationDate,
                expirationDate, description, technicalContact);
    }
}
