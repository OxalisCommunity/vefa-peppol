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

import no.difi.vefa.peppol.common.api.PotentiallySigned;

import java.io.Serializable;

public class Unsigned<T> implements PotentiallySigned<T>, Serializable {

    private static final long serialVersionUID = 2731552303222094156L;

    private final T content;

    public static <T> Unsigned<T> of(T content) {
        return new Unsigned<>(content);
    }

    private Unsigned(T content) {
        this.content = content;
    }

    @Override
    public T getContent() {
        return content;
    }

    @Override
    public <S> Unsigned<S> ofSubset(S s) {
        return new Unsigned<>(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Unsigned<?> unsigned = (Unsigned<?>) o;

        return content.equals(unsigned.content);

    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public String toString() {
        return "Unsigned{" +
                "content=" + content +
                '}';
    }
}
