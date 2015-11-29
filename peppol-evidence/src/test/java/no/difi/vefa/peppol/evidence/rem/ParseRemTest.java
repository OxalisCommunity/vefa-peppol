package no.difi.vefa.peppol.evidence.rem;

import org.etsi.uri._02640.v2_.REMEvidenceType;
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

        assertEquals(value.getEventCode(), EventCode.DELIVERY.value.toString());
    }
}
