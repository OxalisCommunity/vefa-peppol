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
