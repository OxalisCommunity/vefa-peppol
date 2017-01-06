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
import java.util.Arrays;

public class Receipt implements Serializable {

    private static final long serialVersionUID = -2334768925814974368L;

    private String type;

    private byte[] value;

    public static Receipt of(String type, byte[] value) {
        return new Receipt(type, value);
    }

    public static Receipt of(byte[] value) {
        return of(null, value);
    }

    private Receipt(String type, byte[] value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Receipt receipt = (Receipt) o;

        if (type != null ? !type.equals(receipt.type) : receipt.type != null) return false;
        return Arrays.equals(value, receipt.value);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "type='" + type + '\'' +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
