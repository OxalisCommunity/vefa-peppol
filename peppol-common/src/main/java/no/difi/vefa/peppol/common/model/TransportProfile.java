package no.difi.vefa.peppol.common.model;

import java.io.Serializable;

public class TransportProfile implements Serializable {

    public static final TransportProfile START = new TransportProfile("busdox-transport-start");

    public static final TransportProfile AS2_1_0 = new TransportProfile("busdox-transport-as2-ver1p0");

    public static final TransportProfile AS4 = new TransportProfile("bdxr-transport-ebms3-as4-v1p0"); // busdox-transport-ebms3-as4

    private static final long serialVersionUID = -8215053834194901976L;

    private String identifier;

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
