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

package network.oxalis.vefa.peppol.lookup.provider;

import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.util.ModelUtils;
import network.oxalis.vefa.peppol.lookup.api.MetadataProvider;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultProvider implements MetadataProvider {

    public static final int SEPARATOR_LENGTH = 2;
    public static final String WILDCARD_INDICATOR_CHARACTER = "*";
    public static final String NARROWER_SCHEMEPART_INDICATOR_CHARACTER = "@";

    @Override
    public List<URI> resolveDocumentIdentifiers(URI location, ParticipantIdentifier participant) {
        List<URI> resolvedDocumentIdentifiersURIList = new ArrayList<URI>();
        resolvedDocumentIdentifiersURIList.add(location.resolve(String.format("/%s", participant.urlencoded())));
        return resolvedDocumentIdentifiersURIList;
    }

    @Override
    public List<URI> resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier,
                                            DocumentTypeIdentifier documentTypeIdentifier) {

        List<URI> resolvedServiceMetaDataURIList = new ArrayList<URI>();
        String documentTypeSchemeIdentifier = documentTypeIdentifier.getScheme().getIdentifier();

        if (documentTypeSchemeIdentifier.equals(DocumentTypeIdentifier.DOCUMENT_TYPE_SCHEME_PEPPOL_DOCTYPE_WILDCARD)) {
            String instanceIdentifier = documentTypeIdentifier.getIdentifier();

            int customizationValueStartIndex = instanceIdentifier.indexOf(DocumentTypeIdentifier.SYNTAX_SUBTYPE_SEPARATOR) + SEPARATOR_LENGTH;
            int customizationValueEndIndex = instanceIdentifier.lastIndexOf(DocumentTypeIdentifier.IDENTIFIER_SEPARATOR);

            String syntaxSpecificId = instanceIdentifier.substring(0, customizationValueStartIndex - SEPARATOR_LENGTH);
            String customizationIdentifier = instanceIdentifier.substring(customizationValueStartIndex, customizationValueEndIndex);

            String version = instanceIdentifier.substring(customizationValueEndIndex + SEPARATOR_LENGTH);
            String customizationIdentifierWithWildCardCharacter = customizationIdentifier + WILDCARD_INDICATOR_CHARACTER;
            String resolvedPeppolWildCardDocTypeDocumentIdentifierUrlEncoded = ModelUtils.urlencode("%s::%s##%s::%s",
                    documentTypeSchemeIdentifier, syntaxSpecificId, customizationIdentifierWithWildCardCharacter, version);

            resolvedServiceMetaDataURIList.add(location.resolve(String.format("/%s/services/%s", participantIdentifier.urlencoded(), resolvedPeppolWildCardDocTypeDocumentIdentifierUrlEncoded)));

            while (customizationIdentifier.contains(NARROWER_SCHEMEPART_INDICATOR_CHARACTER)) {
                String trimmedCustomizationIdentifier = customizationIdentifier.substring(0,
                        customizationIdentifier.lastIndexOf((NARROWER_SCHEMEPART_INDICATOR_CHARACTER)));

                String trimmedCustomizationIdentifierWithWildCardCharacter = trimmedCustomizationIdentifier + WILDCARD_INDICATOR_CHARACTER;
                String trimmedResolvedDocumentIdentifierUrlEncoded = ModelUtils.urlencode("%s::%s##%s::%s",
                        documentTypeSchemeIdentifier, syntaxSpecificId, trimmedCustomizationIdentifierWithWildCardCharacter, version);

                resolvedServiceMetaDataURIList.add(location.resolve(String.format("/%s/services/%s", participantIdentifier.urlencoded(), trimmedResolvedDocumentIdentifierUrlEncoded)));
                customizationIdentifier = trimmedCustomizationIdentifier;
            }
        }
        resolvedServiceMetaDataURIList.add(location.resolve(String.format("/%s/services/%s", participantIdentifier.urlencoded(), documentTypeIdentifier.urlencoded())));
        return resolvedServiceMetaDataURIList;
    }
}
