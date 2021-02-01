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

package network.oxalis.vefa.peppol.lookup.api;

import network.oxalis.vefa.peppol.common.api.PotentiallySigned;
import network.oxalis.vefa.peppol.common.model.ServiceMetadata;
import network.oxalis.vefa.peppol.common.model.ServiceReference;
import network.oxalis.vefa.peppol.security.lang.PeppolSecurityException;

import java.util.List;

public interface MetadataReader {

    List<ServiceReference> parseServiceGroup(FetcherResponse fetcherResponse) throws LookupException;

    PotentiallySigned<ServiceMetadata> parseServiceMetadata(FetcherResponse fetcherResponse)
            throws LookupException, PeppolSecurityException;
}
