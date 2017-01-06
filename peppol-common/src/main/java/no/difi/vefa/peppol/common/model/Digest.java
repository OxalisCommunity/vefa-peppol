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

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Digest{" +
                "method=" + method +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
