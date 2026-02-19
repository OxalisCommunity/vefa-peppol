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

import org.testng.annotations.Test;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class UblDateTimeUtilTest {

    @Test
    public void toXmlDateShouldConvertLocalDateCorrectly() {

        LocalDate date = LocalDate.of(2025, 3, 15);

        XMLGregorianCalendar xmlDate =
                UblDateTimeUtil.toXmlDate(date);

        assertNotNull(xmlDate);

        assertEquals(xmlDate.getYear(), 2025);
        assertEquals(xmlDate.getMonth(), 3);
        assertEquals(xmlDate.getDay(), 15);

        assertEquals(xmlDate.getTimezone(), DatatypeConstants.FIELD_UNDEFINED);
    }

    @Test
    public void toXmlTimeWithOffsetShouldConvertOffsetTimeCorrectly() {

        OffsetTime time =
                OffsetTime.of(14, 30, 45, 123_000_000, ZoneOffset.ofHours(2));

        XMLGregorianCalendar xmlTime =
                UblDateTimeUtil.toXmlTimeWithOffset(time);

        assertNotNull(xmlTime);

        assertEquals(xmlTime.getHour(), 14);
        assertEquals(xmlTime.getMinute(), 30);
        assertEquals(xmlTime.getSecond(), 45);

        assertEquals(xmlTime.getTimezone(), 120);
    }

    @Test
    public void toXmlTimeWithOffsetShouldHandleUtcOffset() {

        OffsetTime utcTime =
                OffsetTime.of(10, 0, 0, 0, ZoneOffset.UTC);

        XMLGregorianCalendar xmlTime =
                UblDateTimeUtil.toXmlTimeWithOffset(utcTime);

        assertEquals(xmlTime.getTimezone(), 0);
    }

    @Test
    public void toXmlTimeWithOffsetShouldHandleNegativeOffset() {

        OffsetTime time =
                OffsetTime.of(8, 15, 0, 0, ZoneOffset.ofHours(-5));

        XMLGregorianCalendar xmlTime =
                UblDateTimeUtil.toXmlTimeWithOffset(time);

        assertEquals(xmlTime.getTimezone(), -300);
    }

    @Test
    public void toXmlDateShouldRoundTripViaToString() {

        LocalDate date = LocalDate.of(2030, 12, 31);

        XMLGregorianCalendar xmlDate =
                UblDateTimeUtil.toXmlDate(date);

        assertEquals(xmlDate.toXMLFormat(), "2030-12-31");
    }
}

