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

package network.oxalis.vefa.peppol.common.model;

import network.oxalis.vefa.peppol.common.lang.PeppolParsingException;

import java.io.Serializable;

/**
 * DocumentTypeIdentifier is a combination of XML type and customizationId.
 */
public class DocumentTypeIdentifier extends AbstractQualifiedIdentifier implements Serializable {

    private static final long serialVersionUID = -3748163459655880167L;


    /**
     * Separator between Identifier and scheme/element
     */
    public static final String IDENTIFIER_SEPARATOR = "::";

    /**
     * Separator between syntax and subtype
     */
    public static final String SYNTAX_SUBTYPE_SEPARATOR = "##";

    /**
     * Default document identifier scheme
     *
     */
    public static final String DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS = "busdox-docid-qns";

    /**
     * The "Wildcard" document type identifier scheme as per Peppol Policy for use of Identifiers 4.2.0.
     *
     */
    public static final String DOCUMENT_TYPE_SCHEME_PEPPOL_DOCTYPE_WILDCARD = "peppol-doctype-wildcard";

    /**
     * Scheme busdox-docid-qns is default Scheme
     * For PINT profile always specify @link #PEPPOL_DOCTYPE_WILDCARD_SCHEME}
     * @deprecated Use {@link #BUSDOX_DOCID_QNS_SCHEME} or {@link #PEPPOL_DOCTYPE_WILDCARD_SCHEME}
     */
    @Deprecated
    public static final Scheme DEFAULT_SCHEME = Scheme.of(DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS);


    public static final Scheme BUSDOX_DOCID_QNS_SCHEME = Scheme.of(DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS);

    public static final Scheme PEPPOL_DOCTYPE_WILDCARD_SCHEME = Scheme.of(DOCUMENT_TYPE_SCHEME_PEPPOL_DOCTYPE_WILDCARD);

    public String getDefaultDocumentTypeIdentifierScheme ()
    {
        return DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS;
    }

    public static DocumentTypeIdentifier of(String identifier) {
        return new DocumentTypeIdentifier(identifier, DEFAULT_SCHEME);
    }

    public static DocumentTypeIdentifier of(String identifier, Scheme scheme) {
        return new DocumentTypeIdentifier(identifier, scheme);
    }

    public static DocumentTypeIdentifier parse(String str) throws PeppolParsingException {
        String[] parts = str.split("::", 2);

        if (parts.length != 2)
            throw new PeppolParsingException(String.format("Unable to parse document type identifier '%s'.", str));

        return of(parts[1], Scheme.of(parts[0]));
    }

    protected DocumentTypeIdentifier(String value, Scheme scheme) {
        super(value, scheme);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentTypeIdentifier that = (DocumentTypeIdentifier) o;

        return toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s::%s", scheme, identifier);
    }
}
