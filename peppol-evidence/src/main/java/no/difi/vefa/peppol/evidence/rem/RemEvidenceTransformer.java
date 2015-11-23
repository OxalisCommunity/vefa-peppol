package no.difi.vefa.peppol.evidence.rem;

import org.etsi.uri._02640.v2_.REMEvidenceType;

import javax.xml.bind.*;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by steinar on 08.11.2015.
 */
public class RemEvidenceTransformer {


    private JAXBContext jaxbContext;
    private boolean formattedOutput = false;

    public RemEvidenceTransformer(JAXBContext jaxbContext) {

        this.jaxbContext = jaxbContext;
    }

    public void transformToXml(JAXBElement<REMEvidenceType> remEvidenceInstance, OutputStream outputStream) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            // This will cause the signed XML document to fail validation
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formattedOutput);
            marshaller.marshal(remEvidenceInstance, outputStream);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create JAXB Marshaller",e);
        }
    }

    public JAXBElement<REMEvidenceType> parse(InputStream inputStream) {

        JAXBElement<REMEvidenceType> result = null;

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object unmarshal = unmarshaller.unmarshal(inputStream);
            if (unmarshal instanceof JAXBElement) {
                result = (JAXBElement<REMEvidenceType>) unmarshal;
            } else {
                throw new IllegalStateException("InputStream converted into unknown Class: " + unmarshal.getClass().getName());
            }

        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to parse input stream", e);
        }
        return result;
    }

    public boolean isFormattedOutput() {
        return formattedOutput;
    }

    public void setFormattedOutput(boolean formattedOutput) {
        this.formattedOutput = formattedOutput;
    }
}
