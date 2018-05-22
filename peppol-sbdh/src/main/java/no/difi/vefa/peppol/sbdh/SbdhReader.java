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

    public static Header read(StandardBusinessDocumentHeader sbdh) throws SbdhException {
        Header header = Header.newInstance();

        // Sender
        if (sbdh.getSender() == null || sbdh.getSender().size() == 0)
            throw new SbdhException("Sender is not provided in SBDH.");

        PartnerIdentification senderIdentifier = sbdh.getSender().get(0).getIdentifier();
        header = header.sender(
                ParticipantIdentifier.of(senderIdentifier.getValue(), Scheme.of(senderIdentifier.getAuthority())));

        // Receiver
        if (sbdh.getReceiver() == null || sbdh.getReceiver().size() == 0)
            throw new SbdhException("Receiver is not provided in SBDH.");

        PartnerIdentification receiverIdentifier = sbdh.getReceiver().get(0).getIdentifier();
        header = header.receiver(
                ParticipantIdentifier.of(receiverIdentifier.getValue(), Scheme.of(receiverIdentifier.getAuthority())));


        DocumentIdentification docIdent = sbdh.getDocumentIdentification();
        if (docIdent == null)
            throw new SbdhException("Document identification is not provided in SBDH.");

        // Identifier
        if (docIdent.getInstanceIdentifier() == null)
            throw new SbdhException("SBDH instance identifier is not provided in SBDH.");

        header = header.identifier(InstanceIdentifier.of(docIdent.getInstanceIdentifier()));

        // InstanceType
        if (docIdent.getStandard() == null || docIdent.getType() == null || docIdent.getTypeVersion() == null)
            throw new SbdhException("Information about standard, type or type version is not provided in SBDH.");

        header = header.instanceType(InstanceType.of(
                docIdent.getStandard(), docIdent.getType(), docIdent.getTypeVersion()));

        // CreationTimestamp
        if (sbdh.getDocumentIdentification().getCreationDateAndTime() == null)
            throw new SbdhException("Element 'CreationDateAndTime' is not set or contains invalid value.");

        header = header.creationTimestamp(
                SbdhHelper.fromXMLGregorianCalendar(sbdh.getDocumentIdentification().getCreationDateAndTime()));

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

        if (header.getDocumentType() == null)
            throw new SbdhException("Scope containing document identifier is not provided in SBDH.");
        if (header.getProcess() == null)
            throw new SbdhException("Scope containing process identifier is not provided in SBDH.");

        return header;
    }
}
