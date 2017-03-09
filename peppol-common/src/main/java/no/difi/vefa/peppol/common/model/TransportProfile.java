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

import java.io.Serializable;

public class TransportProfile implements Serializable {

    private static final long serialVersionUID = -8215053834194901976L;

    public static final TransportProfile START = TransportProfile.of("busdox-transport-start");

    public static final TransportProfile AS2_1_0 = TransportProfile.of("busdox-transport-as2-ver1p0");

    public static final TransportProfile AS4 = TransportProfile.of("bdxr-transport-ebms3-as4-v1p0");

    private String value;

    public static TransportProfile of(String value) {
        return new TransportProfile(value);
    }

    private TransportProfile(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TransportProfile{" + value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransportProfile that = (TransportProfile) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
