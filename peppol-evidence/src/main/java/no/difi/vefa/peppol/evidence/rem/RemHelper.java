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

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.EventReasonType;
import org.etsi.uri._02640.v2_.EventReasonsType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

class RemHelper {

    public static AttributedElectronicAddressType createElectronicAddressType(ParticipantIdentifier participant) {
        AttributedElectronicAddressType o = new AttributedElectronicAddressType();
        o.setValue(participant.toString());
        o.setScheme(participant.getScheme().getValue());

        return o;
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
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            throw new RemEvidenceException(
                    String.format("Unable to create XMLGregorianCalendar instance from '%s'", date), e);
        }
    }
}
