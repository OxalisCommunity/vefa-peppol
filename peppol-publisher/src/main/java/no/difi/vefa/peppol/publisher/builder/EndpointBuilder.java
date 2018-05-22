/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.publisher.builder;

import no.difi.vefa.peppol.common.model.Period;
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
        Period period = null;

        if (activationDate != null || expirationDate != null)
            period = Period.of(activationDate, expirationDate);

        return new PublisherEndpoint(transportProfile, address, certificate, period, description, technicalContact);
    }
}
