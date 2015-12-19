package no.difi.vefa.peppol.common.model;

import java.io.Serializable;
import java.util.UUID;

public class InstanceIdentifier implements Serializable {

    private static final long serialVersionUID = 3616828001672136897L;

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
