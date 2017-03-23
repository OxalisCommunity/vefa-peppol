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

import java.io.Serializable;
import java.util.Locale;

/**
 * Representation of a participant identifier. Immutable object.
 */
public class ParticipantIdentifier extends AbstractQualifiedIdentifier implements Serializable {

    private static final long serialVersionUID = -8052874032415088055L;

    /**
     * Default scheme used when no scheme or ICD specified.
     */
    public static final Scheme DEFAULT_SCHEME = Scheme.of("iso6523-actorid-upis");

    public static ParticipantIdentifier of(String value) {
        return of(value, DEFAULT_SCHEME);
    }

    public static ParticipantIdentifier of(String value, Scheme scheme) {
        return new ParticipantIdentifier(value, scheme);
    }

    public static ParticipantIdentifier parse(String str) throws PeppolParsingException {
        String[] parts = str.split("::", 2);

        if (parts.length != 2)
            throw new PeppolParsingException(String.format("Unable to parse participant identifier '%s'.", str));

        return of(parts[1], Scheme.of(parts[0]));
    }

    /**
     * Creation of participant identifier.
     *
     * @param identifier Normal identifier like '9908:987654321'.
     * @param scheme     Scheme for identifier.
     */
    private ParticipantIdentifier(String identifier, Scheme scheme) {
        super(identifier.trim().toLowerCase(Locale.US), scheme);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipantIdentifier that = (ParticipantIdentifier) o;

        if (!scheme.equals(that.scheme)) return false;
        return identifier.equals(that.identifier);

    }

    @Override
    public int hashCode() {
        int result = scheme.hashCode();
        result = 31 * result + identifier.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s::%s", scheme, identifier);
    }
}
