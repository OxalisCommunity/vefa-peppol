package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.security.api.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;

/**
 * Entry point for most operations pertaining to REM evidence.
 *
 * Provides various services related to production and consumption of REMEvidence,
 * in addition to holding the JAXBContext, which is costly to create.
 *
 * This class is thread safe, however due to the cost of creating the JAXBContext you should wrap it in a singleton
 * if you intend to create numerous instances.
 *
 * Created by steinar on 08.11.2015.
 */
public class RemEvidenceService {

    public RemEvidenceBuilder createDeliveryNonDeliveryToRecipientBuilder() {
        return  new RemEvidenceBuilder(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT);
    }

    public RemEvidenceBuilder createRelayRemMdAcceptanceRejectionBuilder() {
        return new RemEvidenceBuilder(EvidenceTypeInstance.RELAY_REM_MD_ACCEPTANCE_REJECTION);
    }

    public static void verifySignature(SignedRemEvidence signedRemEvidence) throws PeppolSecurityException {
        XmldsigVerifier.verify(signedRemEvidence.getDocument());
    }
}
