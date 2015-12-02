package no.difi.vefa.peppol.evidence.rem;

import eu.peppol.xsd.ticc.receipt._1.PeppolRemExtension;
import org.etsi.uri._02640.v2_.REMEvidenceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

enum JaxbContextHolder {

    INSTANCE;

    private JAXBContext jaxbContext;

    private JaxbContextHolder() {
        try {
            jaxbContext = JAXBContext.newInstance(REMEvidenceType.class, PeppolRemExtension.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create JAXBContext for REMEvidence " + e.getMessage(), e);
        }
    }

    Marshaller getMarshaller() throws JAXBException {
        return jaxbContext.createMarshaller();
    }

    Unmarshaller getUnmarshaller() throws JAXBException {
        return jaxbContext.createUnmarshaller();
    }
}
