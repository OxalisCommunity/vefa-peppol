/*
 * Copyright 2015-2026 Direktoratet for forvaltning og IKT
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

package network.oxalis.vefa.peppol.mls.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.OffsetTime;

public final class UblDateTimeUtil {

    private static final DatatypeFactory FACTORY;

    static {
        try {
            FACTORY = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static XMLGregorianCalendar toXmlDate(LocalDate date) {
        return FACTORY.newXMLGregorianCalendarDate(
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED
        );
    }

    public static XMLGregorianCalendar toXmlTimeWithOffset(OffsetTime offsetTime) {

        try {
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();

            return datatypeFactory.newXMLGregorianCalendarTime(
                    offsetTime.getHour(),
                    offsetTime.getMinute(),
                    offsetTime.getSecond(),
                    DatatypeConstants.FIELD_UNDEFINED,
                    offsetTime.getOffset().getTotalSeconds() / 60
            );

        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("Unable to create XMLGregorianCalendar", e);
        }
    }
}

