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

import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.lookup.reader.MultiReader;
import no.difi.vefa.peppol.publisher.api.ServiceGroupProvider;
import no.difi.vefa.peppol.publisher.api.ServiceMetadataProvider;
import no.difi.vefa.peppol.publisher.builder.EndpointBuilder;
import no.difi.vefa.peppol.publisher.builder.ServiceGroupBuilder;
import no.difi.vefa.peppol.publisher.builder.ServiceMetadataBuilder;
import no.difi.vefa.peppol.publisher.model.PublisherServiceMetadata;
import no.difi.vefa.peppol.publisher.model.ServiceGroup;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * @author erlend
 */
public class PublisherServiceTest {

    private static final DocumentTypeIdentifier DTI_INVOICE = DocumentTypeIdentifier.of(
            "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice" +
                    "##urn:www.cenbii.eu:transaction:biitrns010:ver2.0" +
                    ":extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1");

    private static final ProcessIdentifier PI_INVOICE = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver2.0");


    private ServiceGroupProvider serviceGroupProvider = Mockito.mock(ServiceGroupProvider.class);

    private ServiceMetadataProvider serviceMetadataProvider = Mockito.mock(ServiceMetadataProvider.class);

    private PublisherSyntaxProvider publisherSyntaxProvider = new PublisherSyntaxProvider("bdxr");

    private PublisherService publisherService =
            new PublisherService(serviceGroupProvider, serviceMetadataProvider, publisherSyntaxProvider, null);

    private MetadataReader metadataReader = new MultiReader();

    @BeforeTest
    public void before() {
        Mockito.reset(serviceGroupProvider, serviceMetadataProvider);
    }

    @Test
    public void simpleServiceGroup() throws Exception {
        ServiceGroup serviceGroup = ServiceGroupBuilder.newInstance(ParticipantIdentifier.of("9908:999999999"))
                .add(ServiceReference.of(DTI_INVOICE))
                .build();

        Mockito.when(serviceGroupProvider.get(Mockito.any(ParticipantIdentifier.class)))
                .thenReturn(serviceGroup);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        publisherService.serviceGroup(byteArrayOutputStream, null, URI.create("http://localhost:8080/"),
                ParticipantIdentifier.of("9908:999999999"));

        List<ServiceReference> result = metadataReader.parseServiceGroup(
                new FetcherResponse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), null));

        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).getDocumentTypeIdentifier().toString(), DTI_INVOICE.toString());
    }

    @Test
    public void simpleServiceMetadata() throws Exception {
        PublisherServiceMetadata serviceMetadata = ServiceMetadataBuilder.newInstance()
                .participant(ParticipantIdentifier.of("9908:999888777"))
                .documentTypeIdentifier(DTI_INVOICE)
                .add(PI_INVOICE, EndpointBuilder.newInstance()
                        .transportProfile(TransportProfile.AS2_1_0)
                        .address(URI.create("http://localhost:8080/as2"))
                        .activationDate(new Date())
                        .expirationDate(new Date())
                        .certificate("Test".getBytes())
                        .build())
                .build();

        Mockito.when(serviceMetadataProvider.get(
                Mockito.any(ParticipantIdentifier.class), Mockito.any(DocumentTypeIdentifier.class)))
                .thenReturn(serviceMetadata);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        publisherService.metadataProvider(byteArrayOutputStream, null,
                ParticipantIdentifier.of("9908:999888777"), DTI_INVOICE);

        System.out.println(byteArrayOutputStream.toString());
    }
}
