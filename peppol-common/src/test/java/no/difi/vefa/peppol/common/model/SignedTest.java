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

import java.security.cert.X509Certificate;
import java.util.Date;

public class SignedTest {

    @Test
    public void simple() {
        X509Certificate certificate = Mockito.mock(X509Certificate.class);
        Date date = new Date();

        Signed<String> signed = Signed.of("1", certificate);

        Assert.assertEquals(signed.getContent(), "1");
        Assert.assertNotNull(signed.getCertificate());
        Assert.assertNull(signed.getTimestamp());

        Assert.assertTrue(signed.equals(signed));
        Assert.assertTrue(signed.equals(Signed.of("1", certificate)));
        Assert.assertFalse(signed.equals(Signed.of("1", certificate, date)));
        Assert.assertFalse(signed.equals(Signed.of("2", certificate)));
        Assert.assertFalse(signed.equals(null));
        Assert.assertFalse(signed.equals("1"));
        Assert.assertFalse(signed.equals(Signed.of("1", Mockito.mock(X509Certificate.class))));
        Assert.assertFalse(Signed.of("1", Mockito.mock(X509Certificate.class), date).equals(signed));
        Assert.assertFalse(Signed.of("1", Mockito.mock(X509Certificate.class), date).equals(signed));
        Assert.assertTrue(Signed.of("1", certificate, date).equals(Signed.of("1", certificate, date)));
        Assert.assertFalse(Signed.of("1", certificate, date)
                .equals(Signed.of("1", certificate, new Date(System.currentTimeMillis() + (10 * 1000)))));

        Assert.assertNotNull(signed.hashCode());
        Assert.assertNotNull(Signed.of("1", certificate, date).hashCode());

        Assert.assertTrue(signed.toString().contains("1"));
    }

}
