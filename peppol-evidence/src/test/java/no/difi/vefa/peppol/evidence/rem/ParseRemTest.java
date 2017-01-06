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

import no.difi.vefa.peppol.evidence.jaxb.rem.REMEvidenceType;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 04.11.2015
 *         Time: 19.16
 */
public class ParseRemTest {


    public static final String SAMPLE_REM_XML = "sample-rem.xml";

    /**
     * Uses JAXB to parse the sample REM evidence provided by Jörg Apitzsch.
     *
     * @throws Exception
     */
    @Test
    public void parseSampleRem() throws Exception {

        InputStream sampleRemInputStream = ParseRemTest.class.getClassLoader().getResourceAsStream(SAMPLE_REM_XML);
        assertNotNull(sampleRemInputStream, "Unable to locate " + SAMPLE_REM_XML + " in class path");

        JAXBContext jaxbContext = JAXBContext.newInstance(REMEvidenceType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement unmarshalled = (JAXBElement) unmarshaller.unmarshal(sampleRemInputStream);

        REMEvidenceType value = (REMEvidenceType) unmarshalled.getValue();

        assertEquals(value.getEventCode(), EventCode.DELIVERY.getValue());
    }
}
