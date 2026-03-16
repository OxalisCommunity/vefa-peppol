package network.oxalis.vefa.peppol.lookup.api;

/**
 * A requested PEPPOL resource does not exist.
 * This represents a SUCCESSFUL lookup with a definitive "no" answer —
 * the lookup infrastructure worked correctly, the resource simply isn't there.
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>Participant not registered in SML</li>
 *   <li>Document type not supported by recipient</li>
 *   <li>Endpoint not found for process/transport profile combination</li>
 * </ul>
 */
public class PeppolResourceException extends LookupException {

    public PeppolResourceException(String message) {
        super(message);
    }

    public PeppolResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

