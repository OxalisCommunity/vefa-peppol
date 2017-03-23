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
import java.security.cert.X509Certificate;
import java.util.Date;

public class Signed<T> implements PotentiallySigned<T>, Serializable {

    private static final long serialVersionUID = 4872358438639447851L;

    private final T content;

    private final X509Certificate certificate;

    private final Date timestamp;

    public static <T> Signed<T> of(T content, X509Certificate certificate, Date timestamp) {
        return new Signed<>(content, certificate, timestamp);
    }

    public static <T> Signed<T> of(T content, X509Certificate certificate) {
        return of(content, certificate, null);
    }

    private Signed(T content, X509Certificate certificate, Date timestamp) {
        this.content = content;
        this.certificate = certificate;
        this.timestamp = timestamp;
    }

    @Override
    public T getContent() {
        return content;
    }

    @Override
    public <S> Signed<S> ofSubset(S s) {
        return new Signed<>(s, certificate, timestamp);
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Signed<?> signed = (Signed<?>) o;

        if (!content.equals(signed.content)) return false;
        if (!certificate.equals(signed.certificate)) return false;
        return !(timestamp != null ? !timestamp.equals(signed.timestamp) : signed.timestamp != null);

    }

    @Override
    public int hashCode() {
        int result = content.hashCode();
        result = 31 * result + certificate.hashCode();
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Signed{" +
                "content=" + content +
                ", certificate=" + certificate +
                ", timestamp=" + timestamp +
                '}';
    }
}
