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

package no.difi.vefa.peppol.sbdh;

import no.difi.commons.sbdh.jaxb.*;
import no.difi.vefa.peppol.common.lang.PeppolRuntimeException;
import no.difi.vefa.peppol.common.model.ArgumentIdentifier;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.util.ExceptionUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

interface SbdhHelper {

    JAXBContext JAXB_CONTEXT = ExceptionUtil.perform(PeppolRuntimeException.class, () ->
            JAXBContext.newInstance(StandardBusinessDocument.class, StandardBusinessDocumentHeader.class));

    ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    XMLInputFactory XML_INPUT_FACTORY = ExceptionUtil.perform(PeppolRuntimeException.class, () -> {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        return factory;
    });

    XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();

    DatatypeFactory DATATYPE_FACTORY = ExceptionUtil.perform(PeppolRuntimeException.class, () ->
            DatatypeFactory.newInstance());

    static Partner createPartner(ParticipantIdentifier participant) {
        PartnerIdentification partnerIdentification = new PartnerIdentification();
        partnerIdentification.setAuthority(participant.getScheme().getIdentifier());
        partnerIdentification.setValue(participant.getIdentifier());

        Partner partner = new Partner();
        partner.setIdentifier(partnerIdentification);

        return partner;
    }

    static BusinessScope createBusinessScope(List<Scope> scopes) {
        BusinessScope businessScope = new BusinessScope();
        businessScope.getScope().addAll(scopes);

        return businessScope;
    }

    static Scope createScope(ProcessIdentifier processIdentifier) {
        Scope scope = new Scope();
        scope.setType("PROCESSID");
        scope.setInstanceIdentifier(processIdentifier.getIdentifier());
        if (!processIdentifier.getScheme().equals(ProcessIdentifier.DEFAULT_SCHEME))
            scope.setIdentifier(processIdentifier.getScheme().getIdentifier());

        return scope;
    }

    static Scope createScope(DocumentTypeIdentifier documentTypeIdentifier) {
        Scope scope = new Scope();
        scope.setType("DOCUMENTID");
        scope.setInstanceIdentifier(documentTypeIdentifier.getIdentifier());
        if (!documentTypeIdentifier.getScheme().equals(DocumentTypeIdentifier.DEFAULT_SCHEME))
            scope.setIdentifier(documentTypeIdentifier.getScheme().getIdentifier());

        return scope;
    }

    static Scope createScope(ArgumentIdentifier argumentIdentifier) {
        Scope scope = new Scope();
        scope.setType(argumentIdentifier.getKey());
        scope.setInstanceIdentifier(argumentIdentifier.getIdentifier());

        return scope;
    }

    static XMLGregorianCalendar toXmlGregorianCalendar(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return DATATYPE_FACTORY.newXMLGregorianCalendar(c);
    }

    static Date fromXMLGregorianCalendar(XMLGregorianCalendar calendar) {
        return calendar.toGregorianCalendar().getTime();
    }
}
