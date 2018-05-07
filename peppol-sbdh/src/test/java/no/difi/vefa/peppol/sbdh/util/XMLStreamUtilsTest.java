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

package no.difi.vefa.peppol.sbdh.util;

import com.google.common.io.ByteStreams;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.sbdh.SbdWriter;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Date;

public class XMLStreamUtilsTest {

    @Test
    public void simpleConstructor() {
        new XMLStreamUtils();
    }

    @Test
    public void simpleCopy() throws Exception {
        Header header = Header.newInstance()
                .sender(ParticipantIdentifier.of("9908:987654325"))
                .receiver(ParticipantIdentifier.of("9908:123456785"))
                .process(ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"))
                .documentType(DocumentTypeIdentifier.of(
                        "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice" +
                                "##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0" +
                                ":#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"))
                .instanceType(
                        InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "2.0"))
                .creationTimestamp(new Date())
                .identifier(InstanceIdentifier.generateUUID());

        try (SbdWriter sbdWriter = SbdWriter.newInstance(ByteStreams.nullOutputStream(), header);
             InputStream inputStream = getClass().getResourceAsStream("/ehf-invoice-no-sbdh.xml")) {
            XMLStreamUtils.copy(inputStream, sbdWriter.xmlWriter());
        }
    }
}
