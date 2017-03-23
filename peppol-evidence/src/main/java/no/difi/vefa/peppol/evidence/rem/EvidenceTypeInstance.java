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

package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.evidence.jaxb.rem.REMEvidenceType;

import javax.xml.bind.JAXBElement;

/**
 * REMEvidenceType is an xml complex type, which must be instantiated, see
 * ETSI TS 102 640-2 V2.1.1 section B2
 *
 * @author steinar
 *         Date: 04.11.2015
 *         Time: 19.08
 */
public enum EvidenceTypeInstance {

    RELAY_REM_MD_ACCEPTANCE_REJECTION("RelayREMMDAcceptanceRejection"),
    DELIVERY_NON_DELIVERY_TO_RECIPIENT("DeliveryNonDeliveryToRecipient");

    private final String localName;

    EvidenceTypeInstance(String localName) {
        this.localName = localName;
    }

    public JAXBElement<REMEvidenceType> toJAXBElement(REMEvidenceType remEvidenceType) {
        if (this == RELAY_REM_MD_ACCEPTANCE_REJECTION)
            return RemHelper.OBJECT_FACTORY.createRelayREMMDAcceptanceRejection(remEvidenceType);
        else
            return RemHelper.OBJECT_FACTORY.createDeliveryNonDeliveryToRecipient(remEvidenceType);
    }

    public static EvidenceTypeInstance findByLocalName(String localName) {
        for (EvidenceTypeInstance instance : values())
            if (instance.localName.equals(localName))
                return instance;

        return null;
    }
}
