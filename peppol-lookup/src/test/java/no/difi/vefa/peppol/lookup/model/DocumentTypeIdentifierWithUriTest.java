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

package no.difi.vefa.peppol.lookup.model;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

public class DocumentTypeIdentifierWithUriTest {

    @Test
    public void simple() {
        DocumentTypeIdentifierWithUri documentTypeIdentifierWithUri = DocumentTypeIdentifierWithUri.of(
                "9908:991825827", DocumentTypeIdentifier.DEFAULT_SCHEME, URI.create("http://difi.no/"));

        Assert.assertNotNull(documentTypeIdentifierWithUri.getIdentifier());
        Assert.assertNotNull(documentTypeIdentifierWithUri.getScheme());
        Assert.assertNotNull(documentTypeIdentifierWithUri.getUri());
    }
}
