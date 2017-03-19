/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.icd.code;

import no.difi.vefa.peppol.icd.api.Icd;
import no.difi.vefa.peppol.common.model.Scheme;

public enum PeppolIcd implements Icd {
    FR_SIRENE("FR:SIRENE", "0002"),
    SE_ORGNR("SE:ORGNR", "0007"),
    FR_SIRET("FR:SIRET", "0009"),
    FI_OVT("FI:OVT", "0037"),
    DUNS("DUNS", "0060"),
    GLN("GLN", "0088"),
    DK_P("DK:P", "0096"),
    IT_FTI("IT:FTI", "0097"),
    NL_KVK("NL:KVK", "0106"),
    IT_SIA("IT:SIA", "0135"),
    IT_SECETI("IT:SECETI", "0142"),
    DIGST("DIGST", "0184"),
    DK_CPR("DK:CPR", "9901"),
    DK_CVR("DK:CVR", "9902"),
    DK_SE("DK:SE", "9904"),
    DK_VANS("DK:VANS", "9905"),
    IT_VAT("IT:VAT", "9906"),
    IT_CF("IT:CF", "9907"),
    NO_ORGNR("NO:ORGNR", "9908"),
    NO_VAT("NO:VAT", "9909"),
    HU_VAT("HU:VAT", "9910"),
    @Deprecated
    EU_VAT("EU:VAT", "9912"),
    EU_REID("EU:REID", "9913"),
    AT_VAT("AT:VAT", "9914"),
    AT_GOV("AT:GOV", "9915"),
    @Deprecated
    AT_CID("AT:CID", "9916"),
    IS_KT("IS:KT", "9917"),
    IBAN("IBAN", "9918"),
    AT_KUR("AT:KUR", "9919"),
    ES_VAT("ES:VAT", "9920"),
    IT_IPA("IT:IPA", "9921"),
    AD_VAT("AD:VAT", "9922"),
    AL_VAT("AL:VAT", "9923"),
    BA_VAT("BA:VAT", "9924"),
    BE_VAT("BE:VAT", "9925"),
    BG_VAT("BG:VAT", "9926"),
    CH_VAT("CH:VAT", "9927"),
    CY_VAT("CY:VAT", "9928"),
    CZ_VAT("CZ:VAT", "9929"),
    DE_VAT("DE:VAT", "9930"),
    EE_VAT("EE:VAT", "9931"),
    GB_VAT("GB:VAT", "9932"),
    GR_VAT("GR:VAT", "9933"),
    HR_VAT("HR:VAT", "9934"),
    IE_VAT("IE:VAT", "9935"),
    LI_VAT("LI:VAT", "9936"),
    LT_VAT("LT:VAT", "9937"),
    LU_VAT("LU:VAT", "9938"),
    LV_VAT("LV:VAT", "9939"),
    MC_VAT("MC:VAT", "9940"),
    ME_VAT("ME:VAT", "9941"),
    MK_VAT("MK:VAT", "9942"),
    MT_VAT("MT:VAT", "9943"),
    NL_VAT("NL:VAT", "9944"),
    PL_VAT("PL:VAT", "9945"),
    PT_VAT("PT:VAT", "9946"),
    RO_VAT("RO:VAT", "9947"),
    RS_VAT("RS:VAT", "9948"),
    SI_VAT("SI:VAT", "9949"),
    SK_VAT("SK:VAT", "9950"),
    SM_VAT("SM:VAT", "9951"),
    TR_VAT("TR:VAT", "9952"),
    VA_VAT("VA:VAT", "9953"),
    NL_ION("NL:OIN", "9954"),
    SE_VAT("SE:VAT", "9955"),
    BE_CBE("BE:CBE", "9956"),
    FR_VAT("FR:VAT", "9957"),
    ZZZ("ZZZ", "9999");

    private static final Scheme SCHEME = Scheme.of("iso6523-actorid-upis");

    private final String identifier;

    private final String code;

    PeppolIcd(String identifier, String code) {
        this.identifier = identifier;
        this.code = code;
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
        return SCHEME;
    }

    public static Icd valueOfIcd(String icd) {
        for (PeppolIcd v : values())
            if (v.code.equals(icd))
                return v;

        throw new IllegalArgumentException(String.format("Value '%s' is not valid ICD.", icd));
    }
}
