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

package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.common.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class BusdoxReaderTest {

    private static Logger logger = LoggerFactory.getLogger(BusdoxReaderTest.class);

    private MetadataReader reader = new BusdoxReader();

    @Test
    public void documentIdentifers() throws Exception {
        List<DocumentTypeIdentifier> result = reader.parseDocumentIdentifiers(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);

        for (DocumentTypeIdentifier documentTypeIdentifier : result)
            logger.debug("{}", documentTypeIdentifier);
    }

    @Test
    public void serviceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-991825827.xml"), null));

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");

        try {
            result.getEndpoint(processIdentifier, TransportProfile.START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(result.getEndpoint(processIdentifier, TransportProfile.AS2_1_0));

        assertEquals(
                result.getEndpoint(processIdentifier, TransportProfile.AS2_1_0)
                        .getCertificate().getSubjectDN().toString(),
                "O=EVRY AS, CN=APP_1000000025, C=NO"
        );
    }
}
