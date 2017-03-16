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
