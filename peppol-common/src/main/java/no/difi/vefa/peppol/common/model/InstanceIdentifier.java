package no.difi.vefa.peppol.common.model;

import java.util.UUID;

public class InstanceIdentifier {

    public static InstanceIdentifier generateUUID() {
        return new InstanceIdentifier(UUID.randomUUID().toString());
    }

    private String value;

    public InstanceIdentifier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
