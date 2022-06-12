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

package network.oxalis.vefa.peppol.lookup.model;

import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

public class DocumentTypeIdentifierWithUriTest {

    @Test
    public void simpleWithBusDoxDocIdQNSDocumentTypeIdentifier() {
        DocumentTypeIdentifierWithUri documentTypeIdentifierWithUri = DocumentTypeIdentifierWithUri.of(
                "9908:991825827", DocumentTypeIdentifier.BUSDOX_DOCID_QNS_SCHEME, URI.create("http://difi.no/"));

        Assert.assertNotNull(documentTypeIdentifierWithUri.getIdentifier());
        Assert.assertNotNull(documentTypeIdentifierWithUri.getScheme());
        Assert.assertNotNull(documentTypeIdentifierWithUri.getUri());
    }

    @Test
    public void simpleWithPeppolDoctypeWildCardDocumentTypeIdentifier() {
        DocumentTypeIdentifierWithUri documentTypeIdentifierWithUri = DocumentTypeIdentifierWithUri.of(
                "9908:991825827", DocumentTypeIdentifier.PEPPOL_DOCTYPE_WILDCARD_SCHEME, URI.create("http://difi.no/"));

        Assert.assertNotNull(documentTypeIdentifierWithUri.getIdentifier());
        Assert.assertNotNull(documentTypeIdentifierWithUri.getScheme());
        Assert.assertNotNull(documentTypeIdentifierWithUri.getUri());
    }
}
