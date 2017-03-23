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

    @Test
    public void simpleSubset() {
        X509Certificate certificate = Mockito.mock(X509Certificate.class);

        Signed<String> signed = Signed.of("Text", certificate);

        Assert.assertEquals(signed.getCertificate(), certificate);

        signed = signed.ofSubset(signed.getContent().substring(1));

        Assert.assertEquals(signed.getCertificate(), certificate);
    }
}
