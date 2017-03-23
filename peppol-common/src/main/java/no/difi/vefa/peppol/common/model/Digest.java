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

import no.difi.vefa.peppol.common.code.DigestMethod;

import java.io.Serializable;
import java.util.Arrays;

public class Digest implements Serializable {

    private static final long serialVersionUID = -3084522333478217556L;

    private final DigestMethod method;

    private final byte[] value;

    public static Digest of(DigestMethod method, byte[] value) {
        return new Digest(method, value);
    }

    private Digest(DigestMethod method, byte[] value) {
        this.method = method;
        this.value = value;
    }

    public DigestMethod getMethod() {
        return method;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Digest digest = (Digest) o;

        if (method != digest.method) return false;
        return Arrays.equals(value, digest.value);

    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return "Digest{" +
                "method=" + method +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
