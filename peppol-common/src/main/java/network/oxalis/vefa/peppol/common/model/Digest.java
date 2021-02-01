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

import lombok.Getter;
import lombok.ToString;
import network.oxalis.vefa.peppol.common.code.DigestMethod;

import java.io.Serializable;
import java.util.Arrays;

public interface Digest {

    DigestMethod getMethod();

    byte[] getValue();

    static Digest of(DigestMethod method, byte[] value) {
        return new DefaultDigest(method, value);
    }

    @Getter
    @ToString
    class DefaultDigest implements Digest, Serializable {

        private static final long serialVersionUID = -3084522333478217556L;

        private final DigestMethod method;

        private final byte[] value;

        private DefaultDigest(DigestMethod method, byte[] value) {
            this.method = method;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Digest digest = (Digest) o;

            if (method != digest.getMethod()) return false;
            return Arrays.equals(value, digest.getValue());

        }

        @Override
        public int hashCode() {
            int result = method.hashCode();
            result = 31 * result + Arrays.hashCode(value);
            return result;
        }
    }
}
