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

import no.difi.vefa.peppol.common.api.PerformAction;
import no.difi.vefa.peppol.common.api.PerformResult;
import no.difi.vefa.peppol.common.lang.PeppolRuntimeException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import no.difi.vefa.peppol.common.util.ExceptionUtil;
import no.difi.vefa.peppol.evidence.jaxb.receipt.PeppolRemExtension;
import no.difi.vefa.peppol.evidence.jaxb.rem.AttributedElectronicAddressType;
import no.difi.vefa.peppol.evidence.jaxb.rem.EventReasonType;
import no.difi.vefa.peppol.evidence.jaxb.rem.ObjectFactory;
import no.difi.vefa.peppol.evidence.jaxb.rem.REMEvidenceType;
import no.difi.vefa.peppol.evidence.lang.RemEvidenceException;
import no.difi.vefa.peppol.security.xmldsig.DomUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import java.util.Date;
import java.util.GregorianCalendar;

class RemHelper {

    private static JAXBContext jaxbContext;

    public static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static DatatypeFactory datatypeFactory;

    static {
        ExceptionUtil.perform(PeppolRuntimeException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                jaxbContext = JAXBContext.newInstance(REMEvidenceType.class, PeppolRemExtension.class);
                datatypeFactory = DatatypeFactory.newInstance();
            }
        });
    }

    public static AttributedElectronicAddressType createElectronicAddressType(ParticipantIdentifier participant) {
        AttributedElectronicAddressType o = new AttributedElectronicAddressType();
        o.setValue(participant.getIdentifier());
        o.setScheme(participant.getScheme().getIdentifier());

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
        return ExceptionUtil.perform(RemEvidenceException.class, new PerformResult<DocumentBuilder>() {
            @Override
            public DocumentBuilder action() throws Exception {
                return DomUtils.newDocumentBuilder();
            }
        });
    }
}
