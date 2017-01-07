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

import no.difi.vefa.peppol.common.api.Perform;
import no.difi.vefa.peppol.common.lang.PeppolRuntimeException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import no.difi.vefa.peppol.evidence.jaxb.receipt.PeppolRemExtension;
import no.difi.vefa.peppol.evidence.jaxb.rem.*;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Date;
import java.util.GregorianCalendar;

class RemHelper {

    private static JAXBContext jaxbContext;

    public static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static DatatypeFactory datatypeFactory;

    public static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    static {
        PeppolRuntimeException.verify(new Perform() {
            @Override
            public void action() throws Exception {
                jaxbContext = JAXBContext.newInstance(REMEvidenceType.class, PeppolRemExtension.class);
                datatypeFactory = DatatypeFactory.newInstance();
            }
        });

        DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
    }

    public static AttributedElectronicAddressType createElectronicAddressType(ParticipantIdentifier participant) {
        AttributedElectronicAddressType o = new AttributedElectronicAddressType();
        o.setValue(participant.getIdentifier());
        o.setScheme(participant.getScheme().getValue());

        return o;
    }

    public static ParticipantIdentifier readElectronicAddressType(AttributedElectronicAddressType o) {
        return ParticipantIdentifier.of(o.getValue(), Scheme.of(o.getScheme()));
    }

    public static EventReasonType createEventReasonType(EventReason eventReason) {
        EventReasonType o = new EventReasonType();
        o.setCode(eventReason.getCode());
        o.setDetails(eventReason.getDetails());

        return o;
    }

    public static EventReasonsType createEventReasonsType(EventReason... list) {
        EventReasonsType eventReasonsType = new EventReasonsType();
        for (EventReason o : list)
            eventReasonsType.getEventReason().add(createEventReasonType(o));

        return eventReasonsType;
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws RemEvidenceException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);

        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(c);
        xmlGregorianCalendar.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        return xmlGregorianCalendar;
    }

    public static Date fromXmlGregorianCalendar(XMLGregorianCalendar calendar) {
        return calendar.toGregorianCalendar().getTime();
    }

    public static Marshaller getMarshaller() throws JAXBException {
        return jaxbContext.createMarshaller();
    }

    public static Unmarshaller getUnmarshaller() throws JAXBException {
        return jaxbContext.createUnmarshaller();
    }

    public static DocumentBuilder getDocumentBuilder() throws RemEvidenceException {
        try {
            return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RemEvidenceException(e.getMessage(), e);
        }
    }
}
