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

import com.google.common.io.ByteStreams;
import network.oxalis.vefa.peppol.common.model.*;
import network.oxalis.vefa.peppol.sbdh.util.XMLStreamUtils;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class SbdWriterTest {

    public static final Header header = Header.newInstance()
            .sender(ParticipantIdentifier.of("9908:987654325"))
            .receiver(ParticipantIdentifier.of("9908:123456785"))
            .process(ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"))
            .documentType(DocumentTypeIdentifier.of(
                    "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice" +
                            "##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0" +
                            ":#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0", DocumentTypeIdentifier.BUSDOX_DOCID_QNS_SCHEME ))
            .instanceType(InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "2.0"))
            .creationTimestamp(new Date())
            .identifier(InstanceIdentifier.generateUUID());

    @Test
    public void simpleXml() throws Exception {
        SbdWriter sbdWriter = SbdWriter.newInstance(System.out, header);

        try (InputStream inputStream = getClass().getResourceAsStream("/valid-t10.xml")) {
            XMLStreamUtils.copy(inputStream, sbdWriter.xmlWriter());
        }

        sbdWriter.close();
    }

    @Test
    public void simpleBinary() throws Exception {
        SbdWriter sbdWriter = SbdWriter.newInstance(System.out, header);

        try (InputStream inputStream = getClass().getResourceAsStream("/valid-t10.xml");
             OutputStream outputStream = sbdWriter.binaryWriter("application/xml")) {
            ByteStreams.copy(inputStream, outputStream);
        }

        sbdWriter.close();
    }
}
