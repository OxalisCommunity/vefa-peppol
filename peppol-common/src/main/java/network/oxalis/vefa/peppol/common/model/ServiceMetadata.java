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

import java.io.Serializable;

public class ServiceMetadata extends AbstractServiceMetadata<Endpoint> implements Serializable {

    private static final long serialVersionUID = -7523336374349545534L;

    public static ServiceMetadata of(ServiceInformation<Endpoint> serviceInformation) {
        return new ServiceMetadata(serviceInformation);
    }

    public static ServiceMetadata of(Redirect redirect) {
        return new ServiceMetadata(redirect);
    }

    private ServiceMetadata(ServiceInformation<Endpoint> serviceInformation) {
        super(serviceInformation);
    }

    private ServiceMetadata(Redirect redirect) {
        super(redirect);
    }

}
