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

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceMetadata implements Serializable {

    private static final long serialVersionUID = -7523336374349545534L;

    private ParticipantIdentifier participantIdentifier;

    private DocumentTypeIdentifier documentTypeIdentifier;

    private Set<ProcessIdentifier> processIdentifiers = new HashSet<>();

    private Set<TransportProfile> transportProfiles = new HashSet<>();

    private X509Certificate signer;

    private List<ProcessMetadata> processMetadatas;

    public static ServiceMetadata of(ParticipantIdentifier participantIdentifier,
                                     DocumentTypeIdentifier documentTypeIdentifier, List<ProcessMetadata> processMetadatas,
                                     X509Certificate signer) {
        return new ServiceMetadata(participantIdentifier, documentTypeIdentifier, processMetadatas, signer);
    }

    private ServiceMetadata(ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier,
                            List<ProcessMetadata> processMetadatas, X509Certificate signer) {
        this.participantIdentifier = participantIdentifier;
        this.documentTypeIdentifier = documentTypeIdentifier;
        this.processMetadatas = processMetadatas;
        this.signer = signer;

        for (ProcessMetadata processMetadata : processMetadatas) {
            this.processIdentifiers.add(processMetadata.getProcessIdentifier());
            for (TransportProfile transportProfile : processMetadata.getTransportProfiles()) {
                this.transportProfiles.add(transportProfile);
            }
        }
    }

    public ParticipantIdentifier getParticipantIdentifier() {
        return participantIdentifier;
    }

    public DocumentTypeIdentifier getDocumentTypeIdentifier() {
        return documentTypeIdentifier;
    }

    public List<ProcessIdentifier> getProcessIdentifiers() {
        return new ArrayList<>(processIdentifiers);
    }

    public List<TransportProfile> getTransportProfiles() {
        return new ArrayList<>(transportProfiles);
    }

    public Endpoint getEndpoint(ProcessIdentifier processIdentifier, TransportProfile... transportProfiles)
            throws EndpointNotFoundException {
        for (ProcessMetadata processMetadata : processMetadatas)
            if (processMetadata.getProcessIdentifier().equals(processIdentifier))
                return processMetadata.getEndpoint(transportProfiles);

        throw new EndpointNotFoundException(
                String.format("Combination of '%s' and transport profile(s) not found.", processIdentifier));
    }

    public X509Certificate getSigner() {
        return signer;
    }
}
