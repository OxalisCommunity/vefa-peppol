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

package no.difi.vefa.peppol.publisher;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.publisher.api.PublisherSyntax;
import no.difi.vefa.peppol.publisher.api.ServiceGroupProvider;
import no.difi.vefa.peppol.publisher.api.ServiceMetadataProvider;
import no.difi.vefa.peppol.publisher.lang.PublisherException;
import no.difi.vefa.peppol.publisher.model.PublisherServiceMetadata;
import no.difi.vefa.peppol.publisher.model.ServiceGroup;
import no.difi.vefa.peppol.security.xmldsig.DomUtils;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServlet;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author erlend
 */
public class PublisherService extends HttpServlet {

    private ServiceGroupProvider serviceGroupProvider;

    private ServiceMetadataProvider serviceMetadataProvider;

    private PublisherSyntaxProvider publisherSyntaxProvider;

    private Signer signer;

    public PublisherService(ServiceGroupProvider serviceGroupProvider,
                            ServiceMetadataProvider serviceMetadataProvider,
                            PublisherSyntaxProvider publisherSyntaxProvider,
                            Signer signer) {
        this.serviceGroupProvider = serviceGroupProvider;
        this.serviceMetadataProvider = serviceMetadataProvider;
        this.publisherSyntaxProvider = publisherSyntaxProvider;
        this.signer = signer;
    }

    public void serviceGroup(OutputStream outputStream, String syntax, URI rootUri,
                             ParticipantIdentifier participantIdentifier)
            throws JAXBException, PublisherException {
        ServiceGroup serviceGroup = serviceGroupProvider.get(participantIdentifier);

        PublisherSyntax publisherSyntax = publisherSyntaxProvider.getSyntax(syntax);
        Marshaller marshaller = publisherSyntax.getMarshaller();
        marshaller.marshal(publisherSyntax.of(serviceGroup, rootUri), outputStream);
    }

    public void metadataProvider(OutputStream outputStream, String syntax, ParticipantIdentifier participantIdentifier,
                                 DocumentTypeIdentifier documentTypeIdentifier)
            throws JAXBException, PublisherException {
        PublisherServiceMetadata serviceMetadata =
                serviceMetadataProvider.get(participantIdentifier, documentTypeIdentifier);

        PublisherSyntax publisherSyntax = publisherSyntaxProvider.getSyntax(syntax);
        Marshaller marshaller = publisherSyntax.getMarshaller();

        if (signer == null) {
            marshaller.marshal(publisherSyntax.of(serviceMetadata, false), outputStream);
        } else {
            Document document = DomUtils.newDocumentBuilder().newDocument();
            marshaller.marshal(publisherSyntax.of(serviceMetadata, true), document);
            signer.sign(document, outputStream);
        }
    }
}
