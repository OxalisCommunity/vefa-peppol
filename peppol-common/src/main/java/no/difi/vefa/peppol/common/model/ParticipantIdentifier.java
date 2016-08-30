package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.api.Icd;
import no.difi.vefa.peppol.common.code.Iso6523Icd;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Representation of a participant identifier. Immutable object.
 */
public class ParticipantIdentifier implements Serializable {

    private static final long serialVersionUID = -8052874032415088055L;

    /**
     * Default scheme used when no scheme or ICD specified.
     */
    public static final Scheme DEFAULT_SCHEME = new Scheme("iso6523-actorid-upis");

    private Icd icd;

    private String identifier;

    private String name;

    /**
     * Creation of identifier based on ISO6523 as defined by OpenPEPPOL.
     *
     * @param identifier Normal identifier like '9908:987654321'.
     */
    public ParticipantIdentifier(String identifier) {
        this(identifier, DEFAULT_SCHEME);
    }

    /**
     * Creation of identifier based on ISO6523 as defined by OpenPEPPOL.
     *
     * @param identifier Normal identifier like '9908:987654321'.
     * @param scheme     Scheme for ICD in identifier.
     */
    public ParticipantIdentifier(String identifier, Scheme scheme) {
        identifier = identifier.trim();

        if (identifier.charAt(4) != ':')
            throw new IllegalStateException(String.format("Identifier '%s' not valid.", identifier));
        if (!DEFAULT_SCHEME.equals(scheme))
            throw new IllegalStateException(String.format("Scheme '%s' not recognized.", scheme.getValue()));

        populate(Iso6523Icd.valueOfIcd(identifier.substring(0, 4)), identifier.substring(5));
    }

    /**
     * Creation of identifier.
     *
     * @param icd        ICD used for identifier, like Iso6523Icd.NO_ORGNR.
     * @param identifier Identifier without ICD, like '987654321'.
     */
    public ParticipantIdentifier(Icd icd, String identifier) {
        populate(icd, identifier);
    }

    /**
     * Simple population of fields.
     *
     * @param icd        ICD used for identifier, like Iso6523Icd.NO_ORGNR.
     * @param identifier Identifier without ICD, like '987654321'.
     */
    private void populate(Icd icd, String identifier) {
        this.icd = icd;
        this.identifier = identifier.trim().toLowerCase(Locale.US);
    }

    /**
     * ICD belonging to identifier.
     *
     * @return ICD.
     */
    public Icd getIcd() {
        return icd;
    }

    /**
     * Identifier of participant.
     *
     * @return Identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Scheme of ICD.
     *
     * @return Scheme.
     */
    public Scheme getScheme() {
        return icd.getScheme();
    }

    public String getName() {
        return name;
    }

    public ParticipantIdentifier setName(String name) {
        ParticipantIdentifier participantIdentifier = new ParticipantIdentifier(icd, identifier);
        participantIdentifier.name = name;

        return participantIdentifier;
    }

    /**
     * Returns full identifier, like 'iso6523-actorid-upis::9908:987654321'.
     *
     * @return URL-encoded full identifier.
     */
    public String urlencoded() {
        try {
            return URLEncoder.encode(
                    String.format("%s::%s:%s", icd.getScheme().getValue(), icd.getCode(), identifier), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipantIdentifier that = (ParticipantIdentifier) o;

        if (!icd.equals(that.icd)) return false;
        if (!identifier.equals(that.identifier)) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = icd.hashCode();
        result = 31 * result + identifier.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s:%s", icd.getCode(), identifier);
    }
}
