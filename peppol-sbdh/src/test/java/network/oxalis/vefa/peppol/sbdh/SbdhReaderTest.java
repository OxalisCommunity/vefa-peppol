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

package network.oxalis.vefa.peppol.sbdh;

import network.oxalis.vefa.peppol.common.model.*;
import network.oxalis.vefa.peppol.sbdh.lang.SbdhException;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

public class SbdhReaderTest {

    @Test
    public void simple() throws Exception {
        Header header = Header.newInstance()
                .sender(ParticipantIdentifier.of("9908:987654325"))
                .receiver(ParticipantIdentifier.of("9908:123456785"))
                .process(ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"))
                .documentType(DocumentTypeIdentifier.of(
                        "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice" +
                                "##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0" +
                                ":#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"))
                .c1CountryIdentifier(C1CountryIdentifier.of("NO"))
                .instanceType(InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "2.0"))
                .creationTimestamp(new Date())
                .identifier(InstanceIdentifier.generateUUID());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        SbdhWriter.write(byteArrayOutputStream, header);

        Header parsedHeader = SbdhReader.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        Assert.assertEquals(parsedHeader, header);
    }

    @Test
    public void withProcessScheme() throws Exception {
        Header header = Header.newInstance()
                .sender(ParticipantIdentifier.of("9908:987654325"))
                .receiver(ParticipantIdentifier.of("9908:923829644"))
                .process(ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0", Scheme.of("cenbii-procid-ubl")))
                .documentType(DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote" +
                        "##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1"))
                .c1CountryIdentifier(C1CountryIdentifier.of("NO"))
                .instanceType(InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "CreditNote", "2.1"))
                .creationTimestamp(new Date())
                .identifier(InstanceIdentifier.generateUUID());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        SbdhWriter.write(byteArrayOutputStream, header);

        Header parsedHeader = SbdhReader.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        Assert.assertEquals(parsedHeader, header);
    }

    @Test
    public void withC1CountryCode() throws Exception {
        Header header = Header.newInstance()
                .sender(ParticipantIdentifier.of("0088:7315458756324"))
                .receiver(ParticipantIdentifier.of("0088:4562458856624"))
                .process(ProcessIdentifier.of(">urn:fdc:peppol.eu:2017:poacc:billing:01:1.0", Scheme.of("cenbii-procid-ubl")))
                .documentType(DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice" +
                        "##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"))
                .c1CountryIdentifier(C1CountryIdentifier.of("IN"))
                .instanceType(InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "2.1"))
                .creationTimestamp(new Date())
                .identifier(InstanceIdentifier.generateUUID());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        SbdhWriter.write(byteArrayOutputStream, header);

        Header parsedHeader = SbdhReader.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        Assert.assertEquals(parsedHeader, header);
    }

    @Test(expectedExceptions = SbdhException.class)
    public void triggerExceptionUsingInputStream() throws Exception {
        SbdhReader.read(Mockito.mock(InputStream.class));
    }

    @Test(expectedExceptions = SbdhException.class)
    public void triggerExceptionUsingXMLStreamReader() throws Exception {
        SbdhReader.read(Mockito.mock(XMLStreamReader.class));
    }
}
