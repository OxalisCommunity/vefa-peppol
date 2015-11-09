package no.difi.vefa.peppol.receipt;

import eu.peppol.xsd.ticc.receipt._1.PeppolRemExtensionType;
import org.etsi.uri._02640.v2_.REMEvidenceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Factory for creating RemEvidenceBuilder instances and holding the JAXBContext, which is costly to create.
 *
 * This class is thread safe, however due to the cost of creating the JAXBContext you should wrap it in a singleton
 * if you intend to create numerous instances.
 *
 * <pre>
 *
 * </pre>
 * Created by steinar on 08.11.2015.
 */
public class RemEvidenceService {


    private final JAXBContext jaxbContext;

    public RemEvidenceService() {
        try {
            jaxbContext = JAXBContext.newInstance(REMEvidenceType.class, PeppolRemExtensionType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create JAXBContext ", e);
        }
    }

    RemEvidenceBuilder createDeliveryNonDeliveryToRecipientBuilder() {
        return  new RemEvidenceBuilder(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT, jaxbContext);
    }

    RemEvidenceBuilder createrelayRemMdAcceptanceRejectionBuilder() {
        return new RemEvidenceBuilder(EvidenceTypeInstance.RELAY_REM_MD_ACCEPTANCE_REJECTION, jaxbContext);
    }

    RemEvidenceTransformer createRemEvidenceTransformer() {
        return new RemEvidenceTransformer(jaxbContext);
    }

    /**
     * Provides access to JAXBContext, which was initialized during the construction of this object.
     * It might be useful for creating instances of Marshaller objects, which are not thread safe.
     *
     * @return
     */
    public JAXBContext getJaxbContext() {
        return jaxbContext;
    }
}
