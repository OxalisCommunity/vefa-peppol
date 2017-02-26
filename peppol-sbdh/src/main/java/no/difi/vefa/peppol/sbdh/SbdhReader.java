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

package no.difi.vefa.peppol.sbdh;

import no.difi.commons.sbdh.jaxb.PartnerIdentification;
import no.difi.commons.sbdh.jaxb.Scope;
import no.difi.commons.sbdh.jaxb.StandardBusinessDocumentHeader;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

public class SbdhReader {

    SbdhReader() {

    }

    public static Header read(InputStream inputStream) throws SbdhException {
        try {
            Unmarshaller unmarshaller = SbdhHelper.JAXB_CONTEXT.createUnmarshaller();
            return read(unmarshaller
                    .unmarshal(new StreamSource(inputStream), StandardBusinessDocumentHeader.class).getValue());
        } catch (Exception e) {
            throw new SbdhException(e.getMessage(), e);
        }
    }

    public static Header read(XMLStreamReader xmlStreamReader) throws SbdhException {
        try {
            Unmarshaller unmarshaller = SbdhHelper.JAXB_CONTEXT.createUnmarshaller();
            return read(unmarshaller
                    .unmarshal(xmlStreamReader, StandardBusinessDocumentHeader.class).getValue());
        } catch (Exception e) {
            throw new SbdhException(e.getMessage(), e);
        }
    }

    public static Header read(StandardBusinessDocumentHeader sbdh) {
        Header header = Header.newInstance();

        // Sender
        PartnerIdentification senderIdentifier = sbdh.getSender().get(0).getIdentifier();
        header = header.sender(
                ParticipantIdentifier.of(senderIdentifier.getValue(), Scheme.of(senderIdentifier.getAuthority())));

        // Receiver
        PartnerIdentification receiverIdentifier = sbdh.getReceiver().get(0).getIdentifier();
        header = header.receiver(
                ParticipantIdentifier.of(receiverIdentifier.getValue(), Scheme.of(receiverIdentifier.getAuthority())));

        // Identifier
        header = header.identifier(InstanceIdentifier.of(sbdh.getDocumentIdentification().getInstanceIdentifier()));

        // InstanceType
        header = header.instanceType(InstanceType.of(
                sbdh.getDocumentIdentification().getStandard(),
                sbdh.getDocumentIdentification().getType(),
                sbdh.getDocumentIdentification().getTypeVersion()
        ));

        // CreationTimestamp
        header = header.creationTimestamp(
                SbdhHelper.fromXMLGregorianCalendar(sbdh.getDocumentIdentification().getCreationDateAndTime()));

        // Scope
        for (Scope scope : sbdh.getBusinessScope().getScope()) {
            if (scope.getType().equals("DOCUMENTID")) {
                header = header.documentType(DocumentTypeIdentifier.of(scope.getInstanceIdentifier()));
            } else if (scope.getType().equals("PROCESSID"))
                header = header.process(ProcessIdentifier.of(scope.getInstanceIdentifier()));
        }

        return header;
    }
}
