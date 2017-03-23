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

import no.difi.vefa.peppol.common.SimpleEndpoint;

import java.io.Serializable;
import java.net.URI;
import java.security.cert.X509Certificate;

public class Endpoint implements Serializable, SimpleEndpoint {

    private static final long serialVersionUID = 5892469135654700883L;

    private final TransportProfile transportProfile;

    private final URI address;

    private final X509Certificate certificate;

    public static Endpoint of(TransportProfile transportProfile, URI address, X509Certificate certificate) {
        return new Endpoint(transportProfile, address, certificate);
    }

    private Endpoint(TransportProfile transportProfile, URI address, X509Certificate certificate) {
        this.transportProfile = transportProfile;
        this.address = address;
        this.certificate = certificate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Endpoint endpoint = (Endpoint) o;

        if (!transportProfile.equals(endpoint.transportProfile)) return false;
        if (!address.equals(endpoint.address)) return false;
        return !(certificate != null ? !certificate.equals(endpoint.certificate) : endpoint.certificate != null);

    }

    @Override
    public int hashCode() {
        int result = transportProfile.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + (certificate != null ? certificate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "transportProfile=" + transportProfile +
                ", address=" + address +
                ", certificate=" + certificate +
                '}';
    }
}
