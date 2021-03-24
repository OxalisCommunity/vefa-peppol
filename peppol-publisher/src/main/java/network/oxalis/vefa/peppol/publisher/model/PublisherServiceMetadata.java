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

package network.oxalis.vefa.peppol.publisher.model;

import network.oxalis.vefa.peppol.common.model.AbstractServiceMetadata;
import network.oxalis.vefa.peppol.common.model.Redirect;
import network.oxalis.vefa.peppol.common.model.ServiceInformation;

/**
 * @author erlend
 */
public class PublisherServiceMetadata extends AbstractServiceMetadata<PublisherEndpoint> {

    public static PublisherServiceMetadata of(ServiceInformation<PublisherEndpoint> serviceInformation) {
        return new PublisherServiceMetadata(serviceInformation);
    }

    public static PublisherServiceMetadata of(Redirect redirect) {
        return new PublisherServiceMetadata(redirect);
    }

    private PublisherServiceMetadata(ServiceInformation<PublisherEndpoint> serviceInformation) {
        super(serviceInformation);
    }

    private PublisherServiceMetadata(Redirect redirect) {
        super(redirect);
    }
}
