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
import network.oxalis.peppol.sbdh.jaxb.PartnerIdentification;
import network.oxalis.peppol.sbdh.jaxb.Scope;
import network.oxalis.peppol.sbdh.jaxb.StandardBusinessDocumentHeader;
import network.oxalis.vefa.peppol.common.model.*;
import network.oxalis.vefa.peppol.sbdh.lang.SbdhException;

import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

import static network.oxalis.vefa.peppol.sbdh.lang.SbdhException.notNull;

public interface SbdhReader {

    static Header read(InputStream inputStream) throws SbdhException {
        try {
            XMLStreamReader reader = SbdhHelper.XML_INPUT_FACTORY.createXMLStreamReader(inputStream);
            Unmarshaller unmarshaller = SbdhHelper.JAXB_CONTEXT.createUnmarshaller();
            return read(unmarshaller
                    .unmarshal(reader, StandardBusinessDocumentHeader.class).getValue());
        } catch (Exception e) {
            throw new SbdhException("Unable to unmarshal content to SBDH.", e);
        }
    }

    static Header read(XMLStreamReader xmlStreamReader) throws SbdhException {
        try {
            Unmarshaller unmarshaller = SbdhHelper.JAXB_CONTEXT.createUnmarshaller();
            return read(unmarshaller
                    .unmarshal(xmlStreamReader, StandardBusinessDocumentHeader.class).getValue());
        } catch (Exception e) {
            throw new SbdhException("Unable to unmarshal content to SBDH.", e);
        }
    }

    static Header read(StandardBusinessDocumentHeader sbdh) throws SbdhException {
        Header header = Header.newInstance();

        // Sender
        SbdhException.notNull("Sender is not provided in SBDH.",
                sbdh.getSender());
        SbdhException.notNull("Sender identifier is not provided in SBDH.",
                sbdh.getSender().getIdentifier());

        PartnerIdentification senderIdentifier = sbdh.getSender().getIdentifier();
        header = header.sender(
                ParticipantIdentifier.of(senderIdentifier.getValue(), Scheme.of(senderIdentifier.getAuthority())));

        // Receiver
        SbdhException.notNull("Receiver is not provided in SBDH.",
                sbdh.getReceiver());
        SbdhException.notNull("Receiver identifier is not provided in SBDH.",
                sbdh.getReceiver().getIdentifier());

        PartnerIdentification receiverIdentifier = sbdh.getReceiver().getIdentifier();
        header = header.receiver(
                ParticipantIdentifier.of(receiverIdentifier.getValue(), Scheme.of(receiverIdentifier.getAuthority())));


        // Prepare...
        DocumentIdentification docIdent = sbdh.getDocumentIdentification();
        SbdhException.notNull("Document identification is not provided in SBDH.",
                docIdent);


        // Identifier
        SbdhException.notNull("SBDH instance identifier is not provided in SBDH.",
                docIdent.getInstanceIdentifier());

        header = header.identifier(InstanceIdentifier.of(docIdent.getInstanceIdentifier()));

        // InstanceType
        SbdhException.notNull("Information about standard is not provided in SBDH.",
                docIdent.getStandard());
        SbdhException.notNull("Information about type is not provided in SBDH.",
                docIdent.getType());
        SbdhException.notNull("Information about type version is not provided in SBDH.",
                docIdent.getTypeVersion());

        header = header.instanceType(InstanceType.of(
                docIdent.getStandard(), docIdent.getType(), docIdent.getTypeVersion()));

        // CreationTimestamp
        SbdhException.notNull("Element 'CreationDateAndTime' is not set or contains invalid value.",
                docIdent.getCreationDateAndTime());
        header = header.creationTimestamp(
                SbdhHelper.fromXMLGregorianCalendar(docIdent.getCreationDateAndTime()));

        // Scope
        for (Scope scope : sbdh.getBusinessScope().getScope()) {
            String type = scope.getType().trim();
            switch (type) {
                case "DOCUMENTID": {
                    Scheme scheme = scope.getIdentifier() != null ?
                            Scheme.of(scope.getIdentifier()) : DocumentTypeIdentifier.BUSDOX_DOCID_QNS_SCHEME;
                    header = header.documentType(DocumentTypeIdentifier.of(scope.getInstanceIdentifier(), scheme));
                    break;
                }
                case "PROCESSID": {
                    Scheme scheme = scope.getIdentifier() != null ?
                            Scheme.of(scope.getIdentifier()) : ProcessIdentifier.DEFAULT_SCHEME;
                    header = header.process(ProcessIdentifier.of(scope.getInstanceIdentifier(), scheme));
                    break;
                }
                case "COUNTRY_C1": {
                    header = header.c1CountryIdentifier(C1CountryIdentifier.of(scope.getInstanceIdentifier()));
                    break;
                }
                case "MLS_TO": {
                    Scheme scheme = scope.getIdentifier() != null ?
                            Scheme.of(scope.getIdentifier()) : MlsToIdentifier.DEFAULT_SCHEME;
                    header = header.mlsToIdentifier(MlsToIdentifier.of(scope.getInstanceIdentifier(), scheme));
                    break;
                }
                case "MLS_TYPE": {
                    header = header.mlsTypeIdentifier(MlsTypeIdentifier.of(scope.getInstanceIdentifier()));
                    break;
                }
                default: {
                    header = header.argument(ArgumentIdentifier.of(type, scope.getInstanceIdentifier()));
                    break;
                }
            }
        }

        notNull("Scope containing document identifier is not provided in SBDH.",
                header.getDocumentType());
        notNull("Scope containing process identifier is not provided in SBDH.",
                header.getProcess());

        return header;
    }
}
