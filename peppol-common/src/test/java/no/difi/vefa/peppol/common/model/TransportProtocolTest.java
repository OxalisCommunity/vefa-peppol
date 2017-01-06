/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

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
        Assert.assertFalse(TransportProtocol.AS2.equals(TransportProfile.AS2_1_0));
        Assert.assertFalse(TransportProtocol.AS2.equals(null));
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
