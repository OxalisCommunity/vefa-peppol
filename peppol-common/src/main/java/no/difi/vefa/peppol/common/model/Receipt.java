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

import java.io.Serializable;
import java.util.Arrays;

public class Receipt implements Serializable {

    private static final long serialVersionUID = -2334768925814974368L;

    private final String type;

    private final byte[] value;

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
