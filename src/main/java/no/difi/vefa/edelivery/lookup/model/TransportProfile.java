package no.difi.vefa.edelivery.lookup.model;

public class TransportProfile {

    public static final TransportProfile START = new TransportProfile("busdox-transport-start");
    public static final TransportProfile AS2_1_0 = new TransportProfile("busdox-transport-as2-ver1p0");
    public static final TransportProfile AS4 = new TransportProfile("busdox-transport-ebms3-as4");

    private String identifier;

    public TransportProfile(String identifier) {
        this.identifier = identifier;
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
