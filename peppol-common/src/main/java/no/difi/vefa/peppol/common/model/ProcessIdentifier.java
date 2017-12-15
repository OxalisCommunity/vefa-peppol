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

/**
 * Immutable object.
 */
public class ProcessIdentifier extends AbstractQualifiedIdentifier implements Serializable {

    private static final long serialVersionUID = 7486398061021950763L;

    public static final Scheme DEFAULT_SCHEME = Scheme.of("cenbii-procid-ubl");

    public static final ProcessIdentifier NO_PROCESS =
            ProcessIdentifier.of("bdx:noprocess", Scheme.of("bdx-procid-transport"));

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
