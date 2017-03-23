/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.common.model;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.security.cert.X509Certificate;

public class EndpointTest {

    @Test
    public void simple() {
        Endpoint endpoint1 = Endpoint.of(
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class)
        );
        Endpoint endpoint2 = Endpoint.of(
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                null
        );
        Endpoint endpoint3 = Endpoint.of(
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                null
        );

        Assert.assertEquals(endpoint1.getTransportProfile(), TransportProfile.AS2_1_0);
        Assert.assertEquals(endpoint1.getAddress(), URI.create("https://ap.example.com/as2"));
        Assert.assertNotNull(endpoint1.getCertificate());

        Assert.assertTrue(endpoint1.equals(endpoint1));
        Assert.assertFalse(endpoint1.equals("Endpoint"));
        Assert.assertFalse(endpoint1.equals(null));

        Assert.assertNotNull(endpoint1.hashCode());

        Assert.assertFalse(endpoint1.equals(Endpoint.of(
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class))));

        Assert.assertFalse(endpoint1.equals(Endpoint.of(
                TransportProfile.AS4,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class))));

        Assert.assertFalse(endpoint1.equals(Endpoint.of(
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as"),
                Mockito.mock(X509Certificate.class))));

        Assert.assertFalse(endpoint1.equals(Endpoint.of(
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class))));

        Assert.assertFalse(endpoint1.equals(endpoint2));
        Assert.assertFalse(endpoint2.equals(endpoint1));
        Assert.assertTrue(endpoint2.equals(endpoint3));

        Assert.assertNotNull(endpoint1.toString());
    }
}
