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


    public static Logger log = LoggerFactory.getLogger(TestResourcesTest.class);

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

        log.debug("Size of baos: " + baos.size());
        log.debug("Size of mime message:" + mimeMessage.getSize());

        InputStream rawInputStream = mimeMessage.getRawInputStream();
        int counter = 0;
        while (rawInputStream.read() >= 0) {
            counter++;
        }
        log.debug("Size of raw input stream " + counter);

        // Headers are not part of the MIME message itself, the are however part of the MimeMessage object:
        //
        // MIME-Version: 1.0
        // Content-Type: multipart/signed; protocol="application/pkcs7-signature"; micalg=sha-1; boundary="----=_Part_34_426016548.1445612302735"
        assertEquals(counter, mimeMessage.getSize());

        // Should contain the complete content type with parameters
        String contentType = mimeMessage.getContentType();
        assertTrue(contentType.startsWith("multipart/signed"));

        Enumeration allHeaderLines = mimeMessage.getAllHeaderLines();
        while (allHeaderLines.hasMoreElements()) {
            String s = (String) allHeaderLines.nextElement();
            log.debug(s);
        }
    }
}
