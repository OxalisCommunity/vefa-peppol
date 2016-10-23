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

package no.difi.vefa.peppol.sbdh;

import no.difi.vefa.peppol.common.api.Perform;
import no.difi.vefa.peppol.common.lang.PeppolRuntimeException;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.*;

import javax.xml.bind.JAXBContext;
import javax.xml.datatype.DatatypeConfigurationException;
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

    static {
        PeppolRuntimeException.verify(new Perform() {
            @Override
            public void action() throws Exception {
                JAXB_CONTEXT =
                        JAXBContext.newInstance(StandardBusinessDocument.class, StandardBusinessDocumentHeader.class);

                XML_INPUT_FACTORY = XMLInputFactory.newFactory();

                XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();
            }
        });
    }

    SbdhHelper() {

    }

    public static Partner createPartner(ParticipantIdentifier participant) {
        PartnerIdentification partnerIdentification = new PartnerIdentification();
        partnerIdentification.setAuthority(participant.getScheme().getValue());
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

        return scope;
    }

    public static Scope createScope(DocumentTypeIdentifier documentTypeIdentifier) {
        Scope scope = new Scope();
        scope.setType("DOCUMENTID");
        scope.setInstanceIdentifier(documentTypeIdentifier.getIdentifier());

        return scope;
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws SbdhException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            throw new SbdhException(
                    String.format("Unable to create XMLGregorianCalendar instance from '%s'", date), e);
        }
    }

    public static Date fromXMLGregorianCalendar(XMLGregorianCalendar calendar) {
        return calendar.toGregorianCalendar().getTime();
    }
}
