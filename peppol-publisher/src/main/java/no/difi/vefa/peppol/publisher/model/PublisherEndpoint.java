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

package no.difi.vefa.peppol.publisher.model;

import no.difi.vefa.peppol.common.api.SimpleEndpoint;
import no.difi.vefa.peppol.common.model.Period;
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

    private Period period;

    private String description;

    private String technicalContact;

    public PublisherEndpoint(TransportProfile transportProfile, URI address, byte[] certificate, Period period,
                             String description, String technicalContact) {
        this.transportProfile = transportProfile;
        this.address = address;
        this.certificate = certificate;
        this.period = period;
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

    public Period getPeriod() {
        return period;
    }

    public String getDescription() {
        return description;
    }

    public String getTechnicalContact() {
        return technicalContact;
    }
}
