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
        Assert.assertEquals(documentTypeIdentifier.getScheme().getIdentifier(), "qualifier");

        try {
            DocumentTypeIdentifier.parse("value");
            Assert.fail();
        } catch (PeppolParsingException e) {
            // Valid!
        }
    }
}
