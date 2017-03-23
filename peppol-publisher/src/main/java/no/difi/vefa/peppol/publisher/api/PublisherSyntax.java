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

package no.difi.vefa.peppol.publisher.api;

import no.difi.vefa.peppol.publisher.model.PublisherServiceMetadata;
import no.difi.vefa.peppol.publisher.model.ServiceGroup;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.net.URI;

/**
 * @author erlend
 */
public interface PublisherSyntax {

    JAXBElement<?> of(ServiceGroup serviceGroup, URI rootUri);

    JAXBElement<?> of(PublisherServiceMetadata serviceMetadata, boolean forSigning);

    Marshaller getMarshaller() throws JAXBException;

}
