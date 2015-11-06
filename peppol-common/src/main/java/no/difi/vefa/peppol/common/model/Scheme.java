package no.difi.vefa.peppol.common.model;

public class Scheme {

    private String value;

    public Scheme(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scheme scheme = (Scheme) o;

        return value.equals(scheme.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
