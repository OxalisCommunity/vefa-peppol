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
        Endpoint endpoint = Endpoint.of(
                ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"),
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class)
        );

        Assert.assertEquals(endpoint.getProcessIdentifier(), ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"));
        Assert.assertEquals(endpoint.getTransportProfile(), TransportProfile.AS2_1_0);
        Assert.assertEquals(endpoint.getAddress(), URI.create("https://ap.example.com/as2"));
        Assert.assertNotNull(endpoint.getCertificate());

        Assert.assertTrue(endpoint.equals(endpoint));
        Assert.assertFalse(endpoint.equals("Endpoint"));
        Assert.assertFalse(endpoint.equals(null));

        Assert.assertNotNull(endpoint.hashCode());

        Assert.assertFalse(endpoint.equals(Endpoint.of(
                ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver2.0"),
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class))));

        Assert.assertFalse(endpoint.equals(Endpoint.of(
                ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"),
                TransportProfile.AS4,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class))));

        Assert.assertFalse(endpoint.equals(Endpoint.of(
                ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"),
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as"),
                Mockito.mock(X509Certificate.class))));

        Assert.assertFalse(endpoint.equals(Endpoint.of(
                ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"),
                TransportProfile.AS2_1_0,
                URI.create("https://ap.example.com/as2"),
                Mockito.mock(X509Certificate.class))));
    }
}
