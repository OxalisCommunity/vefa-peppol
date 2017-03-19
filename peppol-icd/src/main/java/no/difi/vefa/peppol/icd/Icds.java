package no.difi.vefa.peppol.icd;

import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import no.difi.vefa.peppol.icd.api.Icd;
import no.difi.vefa.peppol.icd.model.IcdIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author erlend
 */
public class Icds {

    private final List<Icd> values;

    public static Icds of(Icd[]... values) {
        return new Icds(values);
    }

    private Icds(Icd[]... values) {
        List<Icd> icds = new ArrayList<>();
        for (Icd[] v : values)
            icds.addAll(Arrays.asList(v));
        this.values = icds;
    }

    public IcdIdentifier parse(String s) throws PeppolParsingException {
        return parse(ParticipantIdentifier.parse(s));
    }

    public IcdIdentifier parse(ParticipantIdentifier participantIdentifier) throws PeppolParsingException {
        try {
            String[] parts = participantIdentifier.getIdentifier().split(":", 2);
            return IcdIdentifier.of(findBySchemeAndCode(participantIdentifier.getScheme(), parts[0]), parts[1]);
        } catch (IllegalArgumentException e) {
            throw new PeppolParsingException(e.getMessage(), e);
        }
    }

    public IcdIdentifier parse(String icd, String identifier) throws PeppolParsingException {
        try {
            return IcdIdentifier.of(findByIdentifier(icd), identifier);
        } catch (IllegalArgumentException e) {
            throw new PeppolParsingException(e.getMessage(), e);
        }
    }

    public Icd findBySchemeAndCode(Scheme scheme, String code) {
        for (Icd v : values)
            if (v.getCode().equals(code))
                if (v.getScheme().equals(scheme))
                    return v;

        throw new IllegalArgumentException(String.format("Value '%s::%s' is not valid ICD.", scheme, code));
    }

    public Icd findByIdentifier(String identifier) {
        for (Icd v : values)
            if (v.getIdentifier().equals(identifier))
                return v;

        throw new IllegalArgumentException(String.format("Value '%s' is not valid ICD.", identifier));
    }

    public Icd findByCode(String code) {
        for (Icd v : values)
            if (v.getCode().equals(code))
                return v;

        throw new IllegalArgumentException(String.format("Value '%s' is not valid ICD.", code));
    }
}
