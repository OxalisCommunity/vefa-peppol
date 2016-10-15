/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
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

import eu.peppol.xsd.ticc.receipt._1.PeppolRemExtension;
import org.etsi.uri._02640.v2_.REMEvidenceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

enum JaxbContextHolder {

    INSTANCE;

    private JAXBContext jaxbContext;

    private JaxbContextHolder() {
        try {
            jaxbContext = JAXBContext.newInstance(REMEvidenceType.class, PeppolRemExtension.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create JAXBContext for REMEvidence " + e.getMessage(), e);
        }
    }

    Marshaller getMarshaller() throws JAXBException {
        return jaxbContext.createMarshaller();
    }

    Unmarshaller getUnmarshaller() throws JAXBException {
        return jaxbContext.createUnmarshaller();
    }
}
