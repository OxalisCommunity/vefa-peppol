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

package network.oxalis.vefa.peppol.common.model;

import network.oxalis.vefa.peppol.common.api.SimpleEndpoint;

import java.io.Serializable;

public abstract class AbstractServiceMetadata<T extends SimpleEndpoint> implements Serializable {

    private static final long serialVersionUID = -7523336374349545534L;

    private final ServiceInformation<T> serviceInformation;
    private final Redirect redirect;

    protected AbstractServiceMetadata(ServiceInformation<T> serviceInformation) {
        this.serviceInformation = serviceInformation;
        this.redirect = null;
    }

    protected AbstractServiceMetadata(Redirect redirect) {
        this.serviceInformation = null;
        this.redirect = redirect;
    }

    public ServiceInformation<T> getServiceInformation() {
        return serviceInformation;
    }

    public Redirect getRedirect() {
        return redirect;
    }
}
