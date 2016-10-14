package no.difi.vefa.peppol.common.model;

import java.io.Serializable;

public class TransportProfile implements Serializable {

    public static final TransportProfile START = TransportProfile.of("busdox-transport-start");

    public static final TransportProfile AS2_1_0 = TransportProfile.of("busdox-transport-as2-ver1p0");

    public static final TransportProfile AS4 = TransportProfile.of("bdxr-transport-ebms3-as4-v1p0");

    private static final long serialVersionUID = -8215053834194901976L;

    private String identifier;

    public static TransportProfile of(String identifier) {
        return new TransportProfile(identifier);
    }

    @Deprecated
    public TransportProfile(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "TransportProfile{" + identifier + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransportProfile that = (TransportProfile) o;

        return !(identifier != null ? !identifier.equals(that.identifier) : that.identifier != null);

    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }
}
