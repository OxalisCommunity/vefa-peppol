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

package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.api.SimpleEndpoint;

import java.io.Serializable;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Objects;

public class Endpoint implements Serializable, SimpleEndpoint {

    private static final long serialVersionUID = 5892469135654700883L;

    private final TransportProfile transportProfile;

    private final URI address;

    private final X509Certificate certificate;

    private final Period period;

    public static Endpoint of(TransportProfile transportProfile, URI address,
                              X509Certificate certificate) {
        return new Endpoint(transportProfile, address, certificate, null);
    }

    public static Endpoint of(TransportProfile transportProfile, URI address,
                              X509Certificate certificate, Period period) {
        return new Endpoint(transportProfile, address, certificate, period);
    }

    public Endpoint(TransportProfile transportProfile, URI address,
                    X509Certificate certificate, Period period) {
        this.transportProfile = transportProfile;
        this.address = address;
        this.certificate = certificate;
        this.period = period;
    }

    public TransportProfile getTransportProfile() {
        return transportProfile;
    }

    public URI getAddress() {
        return address;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public Period getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return Objects.equals(transportProfile, endpoint.transportProfile) &&
                Objects.equals(address, endpoint.address) &&
                Objects.equals(certificate, endpoint.certificate) &&
                Objects.equals(period, endpoint.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportProfile, address, certificate, period);
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "transportProfile=" + transportProfile +
                ", address=" + address +
                ", certificate=" + certificate +
                ", period=" + period +
                '}';
    }
}
