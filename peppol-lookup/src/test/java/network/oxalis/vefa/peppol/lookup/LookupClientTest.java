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

package network.oxalis.vefa.peppol.lookup;

import network.oxalis.vefa.peppol.common.lang.PeppolException;
import network.oxalis.vefa.peppol.common.model.*;
import network.oxalis.vefa.peppol.lookup.api.NotFoundException;
import network.oxalis.vefa.peppol.lookup.fetcher.ApacheFetcher;
import network.oxalis.vefa.peppol.lookup.fetcher.UrlFetcher;
import network.oxalis.vefa.peppol.lookup.locator.BusdoxLocator;
import network.oxalis.vefa.peppol.mode.Mode;
import network.oxalis.vefa.peppol.security.api.CertificateValidator;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class LookupClientTest {

    private Mode testMode = Mode.of("TEST");

    @Test
    public void simple() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction()
                .fetcher(ApacheFetcher.class)
                .build();

        List<DocumentTypeIdentifier> documentTypeIdentifiers = client.getDocumentIdentifiers(
                ParticipantIdentifier.of("9908:810418052"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(
                ParticipantIdentifier.of("9908:810418052"),
                DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                        "urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0" +
                        ":#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0"));
        assertNotNull(serviceMetadata);
    }

    @Test
    public void simpleHeader() throws PeppolException {
        LookupClient client = LookupClientBuilder.forMode(testMode)
                .fetcher(ApacheFetcher.class)
                .build();

        Endpoint endpoint = client.getEndpoint(
                Header.newInstance()
                        .receiver(ParticipantIdentifier.of("9915:helger"))
                        .documentType(DocumentTypeIdentifier.of(
                                "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                                        "urn:cen.eu:en16931:2017#compliant#" +
                                        "urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"))
                        .process(ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")),
                TransportProfile.PEPPOL_AS4_2_0);

        assertNotNull(endpoint);
    }

    @Test
    public void simpleEndpoint() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest()
                .certificateValidator(CertificateValidator.EMPTY)
                .build();

        Endpoint endpoint = client.getEndpoint(
                ParticipantIdentifier.of("9915:helger"),
                DocumentTypeIdentifier.of(
                        "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                                "urn:cen.eu:en16931:2017#compliant#" +
                                "urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"),
                ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"),
                TransportProfile.PEPPOL_AS4_2_0
        );

        assertNotNull(endpoint);
    }

    @Test
    public void simpleEndpointWithHeader() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest()
                .certificateValidator(CertificateValidator.EMPTY)
                .build();

        Header header = Header.newInstance()
                .sender(ParticipantIdentifier.of("9908:invalid"))
                .receiver(ParticipantIdentifier.of("9915:helger"))
                .process(ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"))
                .documentType(DocumentTypeIdentifier.of(
                        "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                                "urn:cen.eu:en16931:2017#compliant#" +
                                "urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"));

        Endpoint endpoint = client.getEndpoint(header, TransportProfile.PEPPOL_AS4_2_0);

        assertNotNull(endpoint);
    }

    @Test(enabled = false)
    public void simple9915() throws PeppolException {
        LookupClient client = LookupClientBuilder.forTest()
                .fetcher(UrlFetcher.class)
                .build();

        List<DocumentTypeIdentifier> documentTypeIdentifiers =
                client.getDocumentIdentifiers(ParticipantIdentifier.of("9915:setcce-test"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);
    }

    @Test(enabled = false)
    public void simple9933() throws PeppolException {
        LookupClient client = LookupClientBuilder.forProduction()
                .fetcher(UrlFetcher.class)
                .build();

        List<DocumentTypeIdentifier> documentTypeIdentifiers =
                client.getDocumentIdentifiers(ParticipantIdentifier.of("9933:061828591"));

        assertNotNull(documentTypeIdentifiers);
        assertNotEquals(documentTypeIdentifiers.size(), 0);

        ServiceMetadata serviceMetadata = client.getServiceMetadata(
                ParticipantIdentifier.of("9933:061828591"),
                DocumentTypeIdentifier.of(
                        "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##" +
                                "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0" +
                                ":#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"));
        assertNotNull(serviceMetadata);
    }

    @Test(enabled = false, expectedExceptions = NotFoundException.class)
    public void noSmp() throws PeppolException {
        LookupClient client =
                LookupClientBuilder.forMode(testMode)
                        .locator(BusdoxLocator.class)
                        .build();

        List<DocumentTypeIdentifier> dti = client.getDocumentIdentifiers(ParticipantIdentifier.of("9908:no-smp"));
        System.out.println(dti);
    }

    @Test(enabled = false, expectedExceptions = NotFoundException.class)
    public void noSmpApache() throws PeppolException {
        LookupClient client =
                LookupClientBuilder.forMode(testMode)
                        .fetcher(ApacheFetcher.class)
                        .locator(BusdoxLocator.class)
                        .build();

        client.getDocumentIdentifiers(ParticipantIdentifier.of("9908:no-smp"));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void noSml() throws PeppolException {
        LookupClient client =
                LookupClientBuilder.forMode(testMode)
                        .locator(BusdoxLocator.class)
                        .build();

        client.getDocumentIdentifiers(ParticipantIdentifier.of("9908:no-sml"));
    }
}
