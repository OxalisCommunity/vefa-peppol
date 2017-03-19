/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
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
import java.util.List;

public class ServiceMetadata extends AbstractServiceMetadata<Endpoint> implements Serializable {

    private static final long serialVersionUID = -7523336374349545534L;

    public static ServiceMetadata of(ParticipantIdentifier participantIdentifier,
                                     DocumentTypeIdentifier documentTypeIdentifier,
                                     List<ProcessMetadata<Endpoint>> processes) {
        return new ServiceMetadata(participantIdentifier, documentTypeIdentifier, processes);
    }

    private ServiceMetadata(ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier,
                            List<ProcessMetadata<Endpoint>> processes) {
        super(participantIdentifier, documentTypeIdentifier, processes);
    }
}
