/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
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

import no.difi.vefa.peppol.common.lang.PeppolException;

import java.io.Serializable;
import java.util.regex.Pattern;

public class TransportProtocol implements Serializable {

    private static final long serialVersionUID = -5938766453542971103L;

    private static Pattern pattern = Pattern.compile("[\\p{Upper}\\d]+");

    public static final TransportProtocol AS2 = new TransportProtocol("AS2");

    public static final TransportProtocol AS4 = new TransportProtocol("AS4");

    public static final TransportProtocol INTERNAL = new TransportProtocol("INTERNAL");

    private String value;

    public static TransportProtocol of(String value) throws PeppolException {
        if (!pattern.matcher(value).matches())
            throw new PeppolException("Identifier not according to pattern.");

        return new TransportProtocol(value);
    }

    private TransportProtocol(String identifier) {
        this.value = identifier;
    }

    public String getIdentifier() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransportProtocol that = (TransportProtocol) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "TransportProtocol{" + value + '}';
    }
}
