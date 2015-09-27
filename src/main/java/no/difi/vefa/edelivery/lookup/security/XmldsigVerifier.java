package no.difi.vefa.edelivery.lookup.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.dom.DOMResult;
import java.security.cert.X509Certificate;

public class XmldsigVerifier {

    private static Logger logger = LoggerFactory.getLogger(XmldsigVerifier.class);

    private JAXBContext jaxbContext;

    public XmldsigVerifier(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public X509Certificate verify(Object document) {
        try {
            DOMResult domResult = new DOMResult();
            jaxbContext.createMarshaller().marshal(document, domResult);

            // TODO Validate signature
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        // TODO Return certificate
        return null;
    }
}
