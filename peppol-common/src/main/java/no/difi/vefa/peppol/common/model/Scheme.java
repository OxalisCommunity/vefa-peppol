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

import lombok.Getter;
import no.difi.vefa.peppol.common.api.SimpleIdentifier;

import java.io.Serializable;
import java.util.Objects;

public interface Scheme extends SimpleIdentifier {

    Scheme NONE = of("NONE");

    static Scheme of(String identifier) {
        return new DefaultScheme(identifier);
    }

    @Getter
    class DefaultScheme implements Scheme, Serializable {

        private static final long serialVersionUID = -6022267082161778285L;

        private String identifier;

        private DefaultScheme(String identifier) {
            this.identifier = identifier == null ? null : identifier.trim();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof Scheme)) return false;
            Scheme that = (Scheme) o;
            return Objects.equals(identifier, that.getIdentifier());
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }

        @Override
        public String toString() {
            return identifier;
        }
    }
}
