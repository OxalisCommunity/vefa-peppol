package no.difi.vefa.peppol.icd.model;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.icd.api.Icd;

import java.io.Serializable;

/**
 * @author erlend
 */
public class IcdIdentifier implements Serializable {

    private static final long serialVersionUID = -7908081727801249085L;

    private final Icd icd;

    private final String identifier;

    public static IcdIdentifier of(Icd icd, String identifier) {
        return new IcdIdentifier(icd, identifier);
    }

    private IcdIdentifier(Icd icd, String identifier) {
        this.icd = icd;
        this.identifier = identifier;
    }

    public Icd getIcd() {
        return icd;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ParticipantIdentifier toParticipantIdentifier() {
        return ParticipantIdentifier.of(String.format("%s:%s", icd.getCode(), identifier), icd.getScheme());
    }

    public String toString() {
        return String.format("%s::%s:%s", icd.getScheme(), icd.getCode(), identifier);
    }
}