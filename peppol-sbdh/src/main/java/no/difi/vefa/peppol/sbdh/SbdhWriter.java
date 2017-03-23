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

import no.difi.commons.sbdh.jaxb.DocumentIdentification;
import no.difi.commons.sbdh.jaxb.StandardBusinessDocumentHeader;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;

public class SbdhWriter {

    SbdhWriter() {

    }

    public static void write(OutputStream outputStream, Header header) throws SbdhException {
        try {
            XMLStreamWriter streamWriter = SbdhHelper.XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, "UTF-8");
            streamWriter.writeStartDocument("UTF-8", "1.0");
            write(streamWriter, header);
            streamWriter.writeEndDocument();
            streamWriter.close();
        } catch (Exception e) {
            throw new SbdhException("Unable to write SBDH.", e);
        }
    }

    public static void write(XMLStreamWriter streamWriter, Header header) throws SbdhException {
        try {
            StandardBusinessDocumentHeader sbdh = new StandardBusinessDocumentHeader();
            sbdh.setHeaderVersion("1.0");

            // Sender
            sbdh.getSender().add(SbdhHelper.createPartner(header.getSender()));

            // Receiver
            sbdh.getReceiver().add(SbdhHelper.createPartner(header.getReceiver()));

            sbdh.setDocumentIdentification(new DocumentIdentification());
            // Standard
            sbdh.getDocumentIdentification().setStandard(header.getInstanceType().getStandard());
            // TypeVersion
            sbdh.getDocumentIdentification().setTypeVersion(header.getInstanceType().getVersion());
            // Identifier
            sbdh.getDocumentIdentification().setInstanceIdentifier(header.getIdentifier().getIdentifier());
            // Type
            sbdh.getDocumentIdentification().setType(header.getInstanceType().getType());
            // CreationDateAndTime
            sbdh.getDocumentIdentification().setCreationDateAndTime(
                    SbdhHelper.toXmlGregorianCalendar(header.getCreationTimestamp()));

            sbdh.setBusinessScope(SbdhHelper.createBusinessScope(
                            // DocumentID
                            SbdhHelper.createScope(header.getDocumentType()),
                            // ProcessID
                            SbdhHelper.createScope(header.getProcess()))
            );

            // Marshal!
            Marshaller marshaller = SbdhHelper.JAXB_CONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(SbdhHelper.OBJECT_FACTORY.createStandardBusinessDocumentHeader(sbdh), streamWriter);
        } catch (Exception e) {
            throw new SbdhException("Unable to write SBDH.", e);
        }
    }
}
