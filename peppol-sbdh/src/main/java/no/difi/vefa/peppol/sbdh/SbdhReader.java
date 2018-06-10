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
import no.difi.commons.sbdh.jaxb.PartnerIdentification;
import no.difi.commons.sbdh.jaxb.Scope;
import no.difi.commons.sbdh.jaxb.StandardBusinessDocumentHeader;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

import static no.difi.vefa.peppol.sbdh.lang.SbdhException.notNull;

public class SbdhReader {

    SbdhReader() {
        // TODO Fix in Java 8.
    }

    public static Header read(InputStream inputStream) throws SbdhException {
        try {
            XMLStreamReader reader = SbdhHelper.XML_INPUT_FACTORY.createXMLStreamReader(inputStream);
            Unmarshaller unmarshaller = SbdhHelper.JAXB_CONTEXT.createUnmarshaller();
            return read(unmarshaller
                    .unmarshal(reader, StandardBusinessDocumentHeader.class).getValue());
        } catch (Exception e) {
            throw new SbdhException("Unable to unmarshal content to SBDH.", e);
        }
    }

    public static Header read(XMLStreamReader xmlStreamReader) throws SbdhException {
        try {
            Unmarshaller unmarshaller = SbdhHelper.JAXB_CONTEXT.createUnmarshaller();
            return read(unmarshaller
                    .unmarshal(xmlStreamReader, StandardBusinessDocumentHeader.class).getValue());
        } catch (Exception e) {
            throw new SbdhException("Unable to unmarshal content to SBDH.", e);
        }
    }

    public static Header read(StandardBusinessDocumentHeader sbdh) throws SbdhException {
        Header header = Header.newInstance();

        // Sender
        notNull("Sender is not provided in SBDH.",
                sbdh.getSender());
        notNull("Sender identifier is not provided in SBDH.",
                sbdh.getSender().get(0).getIdentifier());

        PartnerIdentification senderIdentifier = sbdh.getSender().get(0).getIdentifier();
        header = header.sender(
                ParticipantIdentifier.of(senderIdentifier.getValue(), Scheme.of(senderIdentifier.getAuthority())));

        // Receiver
        notNull("Receiver is not provided in SBDH.",
                sbdh.getReceiver());
        notNull("Receiver identifier is not provided in SBDH.",
                sbdh.getReceiver().get(0).getIdentifier());

        PartnerIdentification receiverIdentifier = sbdh.getReceiver().get(0).getIdentifier();
        header = header.receiver(
                ParticipantIdentifier.of(receiverIdentifier.getValue(), Scheme.of(receiverIdentifier.getAuthority())));


        // Prepare...
        DocumentIdentification docIdent = sbdh.getDocumentIdentification();
        notNull("Document identification is not provided in SBDH.",
                docIdent);


        // Identifier
        notNull("SBDH instance identifier is not provided in SBDH.",
                docIdent.getInstanceIdentifier());

        header = header.identifier(InstanceIdentifier.of(docIdent.getInstanceIdentifier()));

        // InstanceType
        notNull("Information about standard is not provided in SBDH.",
                docIdent.getStandard());
        notNull("Information about type is not provided in SBDH.",
                docIdent.getType());
        notNull("Information about type version is not provided in SBDH.",
                docIdent.getTypeVersion());

        header = header.instanceType(InstanceType.of(
                docIdent.getStandard(), docIdent.getType(), docIdent.getTypeVersion()));

        // CreationTimestamp
        notNull("Element 'CreationDateAndTime' is not set or contains invalid value.",
                docIdent.getCreationDateAndTime());
        header = header.creationTimestamp(
                SbdhHelper.fromXMLGregorianCalendar(docIdent.getCreationDateAndTime()));

        // Scope
        for (Scope scope : sbdh.getBusinessScope().getScope()) {
            if (scope.getType().equals("DOCUMENTID")) {
                Scheme scheme = scope.getIdentifier() != null ?
                        Scheme.of(scope.getIdentifier()) : DocumentTypeIdentifier.DEFAULT_SCHEME;
                header = header.documentType(DocumentTypeIdentifier.of(scope.getInstanceIdentifier(), scheme));
            } else if (scope.getType().equals("PROCESSID")) {
                Scheme scheme = scope.getIdentifier() != null ?
                        Scheme.of(scope.getIdentifier()) : ProcessIdentifier.DEFAULT_SCHEME;
                header = header.process(ProcessIdentifier.of(scope.getInstanceIdentifier(), scheme));
            }
        }

        notNull("Scope containing document identifier is not provided in SBDH.",
                header.getDocumentType());
        notNull("Scope containing process identifier is not provided in SBDH.",
                header.getProcess());

        return header;
    }
}
