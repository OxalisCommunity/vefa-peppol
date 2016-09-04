package no.difi.vefa.peppol.common.code;

import no.difi.vefa.peppol.common.api.Icd;
import no.difi.vefa.peppol.common.model.Scheme;

public enum Iso6523Icd implements Icd {
    FR_SIRENE("FR:SIRENE", "0002", false),
    SE_ORGNR("SE:ORGNR", "0007", false),
    FR_SIRET("FR:SIRET", "0009", false),
    FI_OVT("FI:OVT", "0037", false),
    DUNS("DUNS", "0060", false),
    GLN("GLN", "0088", false),
    DK_P("DK:P", "0096", false),
    IT_FTI("IT:FTI", "0097", false),
    NL_KVK("NL:KVK", "0106", false),
    IT_SIA("IT:SIA", "0135", false),
    IT_SECETI("IT:SECETI", "0142", false),
    DK_CPR("DK:CPR", "9901", false),
    DK_CVR("DK:CVR", "9902", false),
    DK_SE("DK:SE", "9904", false),
    DK_VANS("DK:VANS", "9905", false),
    IT_VAT("IT:VAT", "9906", false),
    IT_CF("IT:CF", "9907", false),
    NO_ORGNR("NO:ORGNR", "9908", false),
    NO_VAT("NO:VAT", "9909", true),
    HU_VAT("HU:VAT", "9910", false),
    EU_VAT("EU:VAT", "9912", true),
    EU_REID("EU:REID", "9913", false),
    AT_VAT("AT:VAT", "9914", false),
    AT_GOV("AT:GOV", "9915", false),
    AT_CID("AT:CID", "9916", true),
    IS_KT("IS:KT", "9917", false),
    IBAN("IBAN", "9918", false),
    AT_KUR("AT:KUR", "9919", false),
    ES_VAT("ES:VAT", "9920", false),
    IT_IPA("IT:IPA", "9921", false),
    AD_VAT("AD:VAT", "9922", false),
    AL_VAT("AL:VAT", "9923", false),
    BA_VAT("BA:VAT", "9924", false),
    BE_VAT("BE:VAT", "9925", false),
    BG_VAT("BG:VAT", "9926", false),
    CH_VAT("CH:VAT", "9927", false),
    CY_VAT("CY:VAT", "9928", false),
    CZ_VAT("CZ:VAT", "9929", false),
    DE_VAT("DE:VAT", "9930", false),
    EE_VAT("EE:VAT", "9931", false),
    GB_VAT("GB:VAT", "9932", false),
    GR_VAT("GR:VAT", "9933", false),
    HR_VAT("HR:VAT", "9934", false),
    IE_VAT("IE:VAT", "9935", false),
    LI_VAT("LI:VAT", "9936", false),
    LT_VAT("LT:VAT", "9937", false),
    LU_VAT("LU:VAT", "9938", false),
    LV_VAT("LV:VAT", "9939", false),
    MC_VAT("MC:VAT", "9940", false),
    ME_VAT("ME:VAT", "9941", false),
    MK_VAT("MK:VAT", "9942", false),
    MT_VAT("MT:VAT", "9943", false),
    NL_VAT("NL:VAT", "9944", false),
    PL_VAT("PL:VAT", "9945", false),
    PT_VAT("PT:VAT", "9946", false),
    RO_VAT("RO:VAT", "9947", false),
    RS_VAT("RS:VAT", "9948", false),
    SI_VAT("SI:VAT", "9949", false),
    SK_VAT("SK:VAT", "9950", false),
    SM_VAT("SM:VAT", "9951", false),
    TR_VAT("TR:VAT", "9952", false),
    VA_VAT("VA:VAT", "9953", false),
    NL_ION("NL:ION", "9954", false),
    SE_VAT("SE:VAT", "9955", false),
    ZZZ("ZZZ", "9999", false);

    private String identifier;
    private String code;
    private boolean deprecated;

    Iso6523Icd(String identifier, String code, boolean deprecated) {
        this.identifier = identifier;
        this.code = code;
        this.deprecated = deprecated;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Scheme getScheme() {
        return new Scheme("iso6523-actorid-upis");
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    public static Icd valueOfIcd(String icd) {
        for (Iso6523Icd v : values())
            if (v.code.equals(icd))
                return v;

        throw new IllegalArgumentException(String.format("Value '%s' is not valid ICD.", icd));
    }

    public static Icd valueOfIdentifier(String identifier) {
        for (Iso6523Icd v : values())
            if (v.identifier.equals(identifier))
                return v;

        throw new IllegalArgumentException(String.format("Value '%s' is not valid ICD.", identifier));
    }
}
