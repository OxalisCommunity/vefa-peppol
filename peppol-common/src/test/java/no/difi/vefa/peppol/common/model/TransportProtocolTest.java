package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.lang.PeppolException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TransportProtocolTest {

    @Test
    public void simple() throws PeppolException {
        Assert.assertTrue(TransportProtocol.AS2.equals(TransportProtocol.of(TransportProtocol.AS2.getIdentifier())));

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
    }

    private boolean throwsException(String identifier) {
        try {
            TransportProtocol.of(identifier);
            return false;
        } catch (PeppolException e) {
            return true;
        }
    }

}
