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
import no.difi.vefa.peppol.common.api.PerformAction;
import no.difi.vefa.peppol.common.lang.PeppolRuntimeException;
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

class SbdhHelper {

    public static JAXBContext JAXB_CONTEXT;

    public static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public static XMLInputFactory XML_INPUT_FACTORY;

    public static XMLOutputFactory XML_OUTPUT_FACTORY;

    public static DatatypeFactory DATATYPE_FACTORY;

    static {
        ExceptionUtil.perform(PeppolRuntimeException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                JAXB_CONTEXT =
                        JAXBContext.newInstance(StandardBusinessDocument.class, StandardBusinessDocumentHeader.class);

                XML_INPUT_FACTORY = XMLInputFactory.newFactory();
                XML_INPUT_FACTORY.setProperty(XMLInputFactory.SUPPORT_DTD, false);
                XML_INPUT_FACTORY.setProperty("javax.xml.stream.isSupportingExternalEntities", false);

                XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();

                DATATYPE_FACTORY = DatatypeFactory.newInstance();
            }
        });
    }

    SbdhHelper() {

    }

    public static Partner createPartner(ParticipantIdentifier participant) {
        PartnerIdentification partnerIdentification = new PartnerIdentification();
        partnerIdentification.setAuthority(participant.getScheme().getIdentifier());
        partnerIdentification.setValue(participant.getIdentifier());

        Partner partner = new Partner();
        partner.setIdentifier(partnerIdentification);

        return partner;
    }

    public static BusinessScope createBusinessScope(Scope... scopes) {
        BusinessScope businessScope = new BusinessScope();
        for (Scope scope : scopes)
            businessScope.getScope().add(scope);

        return businessScope;
    }

    public static Scope createScope(ProcessIdentifier processIdentifier) {
        Scope scope = new Scope();
        scope.setType("PROCESSID");
        scope.setInstanceIdentifier(processIdentifier.getIdentifier());
        if (!processIdentifier.getScheme().equals(ProcessIdentifier.DEFAULT_SCHEME))
            scope.setIdentifier(processIdentifier.getScheme().getIdentifier());

        return scope;
    }

    public static Scope createScope(DocumentTypeIdentifier documentTypeIdentifier) {
        Scope scope = new Scope();
        scope.setType("DOCUMENTID");
        scope.setInstanceIdentifier(documentTypeIdentifier.getIdentifier());
        if (!documentTypeIdentifier.getScheme().equals(DocumentTypeIdentifier.DEFAULT_SCHEME))
            scope.setIdentifier(documentTypeIdentifier.getScheme().getIdentifier());

        return scope;
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return DATATYPE_FACTORY.newXMLGregorianCalendar(c);
    }

    public static Date fromXMLGregorianCalendar(XMLGregorianCalendar calendar) {
        return calendar.toGregorianCalendar().getTime();
    }
}
