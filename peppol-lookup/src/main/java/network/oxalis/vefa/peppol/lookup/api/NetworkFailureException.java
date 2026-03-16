package network.oxalis.vefa.peppol.lookup.api;

/**
 * Low-level network I/O failure where the fault originates from transient network issues.
 * Either in PEPPOL infrastructure or in the operator's own network, but we cannot determine which.
 *
 * <p>This is distinct from {@link PeppolInfrastructureException} — when we
 * receive a definitive response from PEPPOL services (e.g., HTTP 5xx, DNS TRY_AGAIN),
 * we know the infrastructure is at fault. {@code NetworkFailureException} is for
 * cases where we never got a response at all.</p>
 *
 * <p><b>Operator action:</b> Retry. If persistent, check YOUR network first.</p>
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>SocketTimeoutException — connection or read timed out</li>
 *   <li>SocketException / connection reset — transport-level failure</li>
 *   <li>General IOException with no diagnostic info</li>
 * </ul>
 */
public class NetworkFailureException extends LookupException {

    public NetworkFailureException(String message) {
        super(message);
    }

    public NetworkFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}

