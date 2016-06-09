package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.lang.PeppolException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TransportProtocolTest {

    @Test
    public void simple() throws PeppolException {
        Assert.assertTrue(TransportProtocol.AS2.equals(TransportProtocol.forIdentifier(TransportProtocol.AS2.getIdentifier())));

        Assert.assertTrue(throwsException("As2"));
        Assert.assertTrue(throwsException("as2"));
        Assert.assertTrue(throwsException("as-2"));
        Assert.assertTrue(throwsException("AS2 "));
        Assert.assertTrue(throwsException("AS2-PEPPOL"));
        Assert.assertTrue(throwsException("AS2_1"));

        Assert.assertFalse(throwsException(TransportProtocol.AS2.getIdentifier()));
        Assert.assertFalse(throwsException(TransportProtocol.AS4.getIdentifier()));
        Assert.assertFalse(throwsException(TransportProtocol.INTERNAL.getIdentifier()));
        Assert.assertFalse(throwsException("FUTURE"));

        Assert.assertNotNull(TransportProtocol.AS2.toString());
        Assert.assertNotNull(TransportProtocol.AS2.hashCode());

        Assert.assertTrue(TransportProtocol.AS2.equals(TransportProtocol.AS2));
        Assert.assertFalse(TransportProtocol.AS2.equals(null));
    }

    private boolean throwsException(String identifier) {
        try {
            TransportProtocol.forIdentifier(identifier);
            return false;
        } catch (PeppolException e) {
            return true;
        }
    }

}
