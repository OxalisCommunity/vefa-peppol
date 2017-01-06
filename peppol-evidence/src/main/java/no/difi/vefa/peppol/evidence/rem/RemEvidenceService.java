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

package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;

/**
 * Entry point for most operations pertaining to REM evidence.
 *
 * Provides various services related to production and consumption of REMEvidence,
 * in addition to holding the JAXBContext, which is costly to create.
 *
 * This class is thread safe, however due to the cost of creating the JAXBContext you should wrap it in a singleton
 * if you intend to create numerous instances.
 *
 * Created by steinar on 08.11.2015.
 */
public class RemEvidenceService {

    public RemEvidenceBuilder createDeliveryNonDeliveryToRecipientBuilder() {
        return  new RemEvidenceBuilder(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT);
    }

    public RemEvidenceBuilder createRelayRemMdAcceptanceRejectionBuilder() {
        return new RemEvidenceBuilder(EvidenceTypeInstance.RELAY_REM_MD_ACCEPTANCE_REJECTION);
    }

    public static void verifySignature(SignedRemEvidence signedRemEvidence) throws PeppolSecurityException {
        XmldsigVerifier.verify(signedRemEvidence.getDocument());
    }
}
