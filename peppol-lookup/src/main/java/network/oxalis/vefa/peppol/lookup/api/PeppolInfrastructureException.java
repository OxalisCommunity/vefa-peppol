package network.oxalis.vefa.peppol.lookup.api;

/**
 * PEPPOL infrastructure (SML, SMP, or AP) is not functioning correctly or responding.
 * The requested resource MAY exist, but we cannot confirm it due to service issues
 * on the infrastructure side.
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>SMP returned HTTP 5xx (server error)</li>
 *   <li>SMP rate limiting (HTTP 429)</li>
 *   <li>SML DNS returned TRY_AGAIN (transient DNS failure on PEPPOL infrastructure)</li>
 *   <li>DNS resolution failure for SML/SMP hostnames (UnknownHostException) —
 *       since SML operates via DNS, resolution failure indicates PEPPOL infrastructure problems</li>
 * </ul>
 */
public class PeppolInfrastructureException extends LookupException {

    public PeppolInfrastructureException(String message) {
        super(message);
    }

    public PeppolInfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}

