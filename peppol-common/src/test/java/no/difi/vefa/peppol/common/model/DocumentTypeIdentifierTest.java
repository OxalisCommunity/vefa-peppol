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

package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class DocumentTypeIdentifierTest {

    @Test
    public void simple() {
        String documentIdentifier = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##" +
                "urn:www.cenbii.eu:transaction:biitrns014:ver2.0" +
                ":extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1";
        assertEquals(DocumentTypeIdentifier.of(documentIdentifier).getIdentifier(), documentIdentifier);

        assertEquals(DocumentTypeIdentifier.of(documentIdentifier, Scheme.of("bdx-ubl")).getScheme(), Scheme.of("bdx-ubl"));

        assertTrue(DocumentTypeIdentifier.of(documentIdentifier).urlencoded().contains("CreditNote"));

        DocumentTypeIdentifier documentTypeIdentifier = DocumentTypeIdentifier.of(documentIdentifier);

        assertTrue(documentTypeIdentifier.equals(documentTypeIdentifier));
        assertFalse(documentTypeIdentifier.equals(documentIdentifier));
        assertFalse(documentTypeIdentifier.equals(null));
    }

    @Test
    public void simpleParse() throws Exception {
        DocumentTypeIdentifier documentTypeIdentifier = DocumentTypeIdentifier
                .parse("qualifier::identifier");

        Assert.assertEquals(documentTypeIdentifier.getIdentifier(), "identifier");
        Assert.assertEquals(documentTypeIdentifier.getScheme().getValue(), "qualifier");

        try {
            DocumentTypeIdentifier.parse("value");
            Assert.fail();
        } catch (PeppolParsingException e) {
            // Valid!
        }
    }
}
