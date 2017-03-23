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

public class InstanceType implements Serializable {

    private static final long serialVersionUID = -8577145245367335582L;

    private final String standard;

    private final String type;

    private final String version;

    public static InstanceType of(String standard, String type, String version) {
        return new InstanceType(standard, type, version);
    }

    public InstanceType(String standard, String type, String version) {
        this.standard = standard;
        this.type = type;
        this.version = version;
    }

    public String getStandard() {
        return standard;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InstanceType that = (InstanceType) o;

        if (!standard.equals(that.standard)) return false;
        if (!type.equals(that.type)) return false;
        return version.equals(that.version);

    }

    @Override
    public int hashCode() {
        int result = standard.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s::%s::%s", standard, type, version);
    }
}
