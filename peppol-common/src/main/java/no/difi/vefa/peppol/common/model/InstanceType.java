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

public class InstanceType {

    private String standard;

    private String type;

    private String version;

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
