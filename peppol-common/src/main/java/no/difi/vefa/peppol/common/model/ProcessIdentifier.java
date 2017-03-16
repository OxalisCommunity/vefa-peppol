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

/**
 * Immutable object.
 */
public class ProcessIdentifier extends AbstractQualifiedIdentifier implements Serializable {

    private static final long serialVersionUID = 7486398061021950763L;

    public static final Scheme DEFAULT_SCHEME = Scheme.of("cenbii-procid-ubl");

    public static ProcessIdentifier of(String identifier) {
        return new ProcessIdentifier(identifier, DEFAULT_SCHEME);
    }

    public static ProcessIdentifier of(String identifier, Scheme scheme) {
        return new ProcessIdentifier(identifier, scheme);
    }

    public static ProcessIdentifier parse(String str) throws PeppolParsingException {
        String[] parts = str.split("::", 2);

        if (parts.length != 2)
            throw new PeppolParsingException(String.format("Unable to parse process identifier '%s'.", str));

        return of(parts[1], Scheme.of(parts[0]));
    }

    private ProcessIdentifier(String value, Scheme scheme) {
        super(value, scheme);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessIdentifier that = (ProcessIdentifier) o;

        if (!identifier.equals(that.identifier)) return false;
        return scheme.equals(that.scheme);

    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + scheme.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s::%s", scheme, identifier);
    }
}
