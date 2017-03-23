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

package no.difi.vefa.peppol.sbdh;

import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.Date;

public class SbdhWriterTest {

    private Header header = Header.newInstance()
            .sender(ParticipantIdentifier.of("9908:987654325"))
            .receiver(ParticipantIdentifier.of("9908:123456785"))
            .process(ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0"))
            .documentType(DocumentTypeIdentifier.of(
                    "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice" +
                            "##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0" +
                            ":#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"))
            .instanceType(InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "2.0"))
            .creationTimestamp(new Date())
            .identifier(InstanceIdentifier.generateUUID());

    @Test
    public void simple() throws Exception {
        SbdhWriter.write(System.out, header);
    }

    @Test
    public void simpleConstructor() {
        new SbdhWriter();
    }

    @Test(expectedExceptions = SbdhException.class)
    public void triggerExceptionUsingXMLStreamWriter() throws Exception {
        SbdhWriter.write(Mockito.mock(XMLStreamWriter.class), null);
    }

    @Test(expectedExceptions = SbdhException.class)
    public void triggerExceptionUsingOutputStream() throws Exception {
        SbdhWriter.write(Mockito.mock(OutputStream.class), null);
    }


}
