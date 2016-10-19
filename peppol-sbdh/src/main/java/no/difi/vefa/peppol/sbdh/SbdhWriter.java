/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
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

package no.difi.vefa.peppol.sbdh;

import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
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
        } catch (XMLStreamException e) {
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
            sbdh.getDocumentIdentification().setInstanceIdentifier(header.getIdentifier().getValue());
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
