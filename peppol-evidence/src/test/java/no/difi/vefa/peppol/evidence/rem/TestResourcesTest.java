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

package no.difi.vefa.peppol.evidence.rem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import javax.mail.internet.MimeMessage;

import static org.testng.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Enumeration;

/**
 * @author steinar
 *         Date: 05.11.2015
 *         Time: 17.49
 */
public class TestResourcesTest {


    public static final Logger LOGGER = LoggerFactory.getLogger(TestResourcesTest.class);

    /** Attempts to retrieve the private key held in our test keystore */
    @Test
    public void getPrivateKey() {

        KeyStore.PrivateKeyEntry privateKey = TestResources.getPrivateKey();
        assertNotNull(privateKey);
    }

    @Test
    public void loadMimeMessage() throws Exception {

        MimeMessage mimeMessage = TestResources.getMimeMessageFromResource("as2-mdn-smime.txt");
        assertNotNull(mimeMessage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mimeMessage.writeTo(baos);

        LOGGER.debug("Size of baos: " + baos.size());
        LOGGER.debug("Size of mime message:" + mimeMessage.getSize());

        InputStream rawInputStream = mimeMessage.getRawInputStream();
        int counter = 0;
        while (rawInputStream.read() >= 0) {
            counter++;
        }
        LOGGER.debug("Size of raw input stream " + counter);

        // Headers are not part of the MIME message itself, the are however part of the MimeMessage object:
        //
        // MIME-Version: 1.0
        // Content-Type: multipart/signed; protocol="application/pkcs7-signature"; micalg=sha-1;
        // boundary="----=_Part_34_426016548.1445612302735"
        assertEquals(counter, mimeMessage.getSize());

        // Should contain the complete content type with parameters
        String contentType = mimeMessage.getContentType();
        assertTrue(contentType.startsWith("multipart/signed"));

        Enumeration allHeaderLines = mimeMessage.getAllHeaderLines();
        while (allHeaderLines.hasMoreElements()) {
            String s = (String) allHeaderLines.nextElement();
            LOGGER.debug(s);
        }
    }
}
