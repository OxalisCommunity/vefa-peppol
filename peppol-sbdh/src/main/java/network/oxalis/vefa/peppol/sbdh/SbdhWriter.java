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

import network.oxalis.peppol.sbdh.jaxb.DocumentIdentification;
import network.oxalis.peppol.sbdh.jaxb.Scope;
import network.oxalis.peppol.sbdh.jaxb.StandardBusinessDocumentHeader;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.sbdh.lang.SbdhException;

import jakarta.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public interface SbdhWriter {

    static void write(OutputStream outputStream, Header header) throws SbdhException {
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

    static void write(XMLStreamWriter streamWriter, Header header) throws SbdhException {
        try {
            StandardBusinessDocumentHeader sbdh = new StandardBusinessDocumentHeader();
            sbdh.setHeaderVersion("1.0");

            // Sender
            sbdh.setSender(SbdhHelper.createPartner(header.getSender()));

            // Receiver
            sbdh.setReceiver(SbdhHelper.createPartner(header.getReceiver()));

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

            List<Scope> scopes = new ArrayList<>();
            // DocumentID
            scopes.add(SbdhHelper.createScope(header.getDocumentType()));
            // ProcessID
            scopes.add(SbdhHelper.createScope(header.getProcess()));
            // C1CountryIdentifier
            scopes.add(SbdhHelper.createScope(header.getC1CountryIdentifier()));

            // MLS_TO is Not mandatory yet
            if(null != header.getMlsToIdentifier()){
                scopes.add(SbdhHelper.createScope(header.getMlsToIdentifier()));
            }

            // MLS_TYPE is Not mandatory yet
            if(null != header.getMlsTypeIdentifier()){
                scopes.add(SbdhHelper.createScope(header.getMlsTypeIdentifier()));
            }

            // Extras
            header.getArguments().forEach(ai -> scopes.add(SbdhHelper.createScope(ai)));

            sbdh.setBusinessScope(SbdhHelper.createBusinessScope(scopes));

            // Marshal!
            Marshaller marshaller = SbdhHelper.JAXB_CONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(SbdhHelper.OBJECT_FACTORY.createStandardBusinessDocumentHeader(sbdh), streamWriter);
        } catch (Exception e) {
            throw new SbdhException("Unable to write SBDH.", e);
            //e.printStackTrace();
        }
    }
}
