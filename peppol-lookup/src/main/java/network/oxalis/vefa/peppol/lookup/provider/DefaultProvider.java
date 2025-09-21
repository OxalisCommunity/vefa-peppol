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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DefaultProvider implements MetadataProvider {

    private static final int SEPARATOR_LENGTH = 2;
    private static final String WILDCARD_INDICATOR_CHARACTER = "*";
    private static final String NARROWER_SCHEMEPART_INDICATOR_CHARACTER = "@";
    private static final String PINT_TEXT = "urn:peppol:pint:";

    @Override
    public List<URI> resolveDocumentIdentifiers(URI location, ParticipantIdentifier participant) {
        return List.of(location.resolve("/" + participant.urlencoded()));
    }

    @Override
    public List<URI> resolveServiceMetadata(URI location,
                                            ParticipantIdentifier participantIdentifier,
                                            DocumentTypeIdentifier documentTypeIdentifier) {
        List<URI> resolvedServiceMetaDataURIList = new ArrayList<>();
        String docSchemeIdentifier = documentTypeIdentifier.getScheme().getIdentifier();

        boolean isPintMessage = isItPINTMessage(documentTypeIdentifier);

        if (DocumentTypeIdentifier.DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS.equals(docSchemeIdentifier)
                && !isPintMessage) {
            addResolvedUri(location, participantIdentifier, documentTypeIdentifier, resolvedServiceMetaDataURIList);
        } else if (isPintMessage) {
            resolvePintExactMatchPriority(location, participantIdentifier, documentTypeIdentifier, resolvedServiceMetaDataURIList);

            if (DocumentTypeIdentifier.DOCUMENT_TYPE_SCHEME_PEPPOL_DOCTYPE_WILDCARD.equals(docSchemeIdentifier)) {
                processPintWildcards(location, participantIdentifier, documentTypeIdentifier, resolvedServiceMetaDataURIList);
            }
        }

        return resolvedServiceMetaDataURIList;
    }

    private boolean isItPINTMessage(DocumentTypeIdentifier documentTypeIdentifier) {
        String customizationId = getCustomizationIdentifier(documentTypeIdentifier);
        return customizationId.contains(PINT_TEXT);
    }

    private String getCustomizationIdentifier(DocumentTypeIdentifier documentTypeIdentifier) {
        String instanceId = documentTypeIdentifier.getIdentifier();
        int startIdx = instanceId.indexOf(DocumentTypeIdentifier.SYNTAX_SUBTYPE_SEPARATOR);
        int endIdx = instanceId.lastIndexOf(DocumentTypeIdentifier.IDENTIFIER_SEPARATOR);

        if (startIdx == -1 || endIdx == -1 || startIdx + SEPARATOR_LENGTH >= endIdx) {
            throw new IllegalArgumentException(String.format("Unable to parse '%s' into Peppol Document Type Identifier", instanceId));
        }

        return instanceId.substring(startIdx + SEPARATOR_LENGTH, endIdx);
    }

    private void resolvePintExactMatchPriority(URI location,
                                               ParticipantIdentifier participantIdentifier,
                                               DocumentTypeIdentifier documentTypeIdentifier,
                                               List<URI> resolvedServiceMetaDataURIList) {
        addResolvedUri(location, participantIdentifier, documentTypeIdentifier, resolvedServiceMetaDataURIList);
    }

    private void processPintWildcards(URI location,
                                      ParticipantIdentifier participantIdentifier,
                                      DocumentTypeIdentifier documentTypeIdentifier,
                                      List<URI> resolvedServiceMetaDataURIList) {
        String instanceId = documentTypeIdentifier.getIdentifier();
        int startIdx = instanceId.indexOf(DocumentTypeIdentifier.SYNTAX_SUBTYPE_SEPARATOR);
        int endIdx = instanceId.lastIndexOf(DocumentTypeIdentifier.IDENTIFIER_SEPARATOR);

        if (startIdx == -1 || endIdx == -1 || startIdx + SEPARATOR_LENGTH >= endIdx) {
            return;
        }

        String syntaxId = instanceId.substring(0, startIdx);
        String customizationId = instanceId.substring(startIdx + SEPARATOR_LENGTH, endIdx);
        String version = "";
        if (endIdx + SEPARATOR_LENGTH <= instanceId.length()) {
            version = instanceId.substring(endIdx + SEPARATOR_LENGTH);
        }

        addWildcardUris(location, participantIdentifier,
                documentTypeIdentifier.getScheme().getIdentifier(),
                syntaxId, customizationId, version, resolvedServiceMetaDataURIList);
    }

    private void addWildcardUris(URI location,
                                 ParticipantIdentifier participantIdentifier,
                                 String docScheme,
                                 String syntaxId,
                                 String customizationId,
                                 String version,
                                 List<URI> resolvedServiceMetaDataURIList) {
        while (!customizationId.isEmpty()) {
            String urlEncoded = ModelUtils.urlencode("%s::%s##%s::%s", docScheme, syntaxId,
                    customizationId + WILDCARD_INDICATOR_CHARACTER, version);

            resolvedServiceMetaDataURIList.add(location.resolve(String.format("/%s/services/%s",
                    participantIdentifier.urlencoded(), urlEncoded)));

            int lastIdx = customizationId.lastIndexOf(NARROWER_SCHEMEPART_INDICATOR_CHARACTER);
            if (lastIdx == -1) break;
            customizationId = customizationId.substring(0, lastIdx);
        }
    }

    private void addResolvedUri(URI location,
                                ParticipantIdentifier participantIdentifier,
                                DocumentTypeIdentifier documentTypeIdentifier,
                                List<URI> resolvedServiceMetaDataURIList) {
        resolvedServiceMetaDataURIList.add(location.resolve(String.format("/%s/services/%s",
                participantIdentifier.urlencoded(), documentTypeIdentifier.urlencoded())));
    }
}

