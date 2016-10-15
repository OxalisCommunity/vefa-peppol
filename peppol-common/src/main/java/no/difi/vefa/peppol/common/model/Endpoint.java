/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.common.model;

import java.io.Serializable;
import java.security.cert.X509Certificate;

public class Endpoint implements Serializable {

    private static final long serialVersionUID = 5892469135654700883L;

    private ProcessIdentifier processIdentifier;

    private TransportProfile transportProfile;

    private String address;

    private X509Certificate certificate;

    public static Endpoint of(ProcessIdentifier processIdentifier, TransportProfile transportProfile, String address,
                              X509Certificate certificate) {
        return new Endpoint(processIdentifier, transportProfile, address, certificate);
    }

    @Deprecated
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
