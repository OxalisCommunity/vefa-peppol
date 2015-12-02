package no.difi.vefa.peppol.evidence.rem;

import eu.peppol.xsd.ticc.receipt._1.PeppolRemExtension;
import org.etsi.uri._02640.v2_.REMEvidenceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

class JaxbContextHolder {

    private static JAXBContext jaxbContext;

    private static synchronized JAXBContext getJaxbContext() throws JAXBException {
        if (jaxbContext == null)
            jaxbContext = JAXBContext.newInstance(REMEvidenceType.class, PeppolRemExtension.class);

        return jaxbContext;
    }

    static Marshaller getMarshaller() throws JAXBException {
        return getJaxbContext().createMarshaller();
    }

    static Unmarshaller getUnmarshaller() throws JAXBException {
        return getJaxbContext().createUnmarshaller();
    }
}
