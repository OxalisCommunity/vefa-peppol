/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.icd.code;

import no.difi.vefa.peppol.icd.api.Icd;
import no.difi.vefa.peppol.common.model.Scheme;

public enum PeppolIcd implements Icd {
    FR_SIRENE("FR:SIRENE", "0002", "Institut National de la Statistique et des Etudes Economiques, (I.N.S.E.E.)"),
    SE_ORGNR("SE:ORGNR", "0007", "The National Tax Board"),
    FR_SIRET("FR:SIRET", "0009", "DU PONT DE NEMOURS"),
    FI_OVT("FI:OVT", "0037", "National Board of Taxes, (Verohallitus)"),
    DUNS("DUNS", "0060", "Dun and Bradstreet Ltd"),
    GLN("GLN", "0088", "GS1 GLN"),
    DK_P("DK:P", "0096", "Danish Chamber of Commerce"),
    IT_FTI("IT:FTI", "0097", "FTI - Ediforum Italia"),
    NL_KVK("NL:KVK", "0106", "Vereniging van Kamers van Koophandel en Fabrieken in Nederland, Scheme"),
    NAL("NAL", "0130", "Directorates of the European Commission"),
    IT_SIA("IT:SIA", "0135", "SIA-Società Interbancaria per l'Automazione S.p.A."),
    IT_SECETI("IT:SECETI", "0142", "Servizi Centralizzati SECETI S.p.A."),
    DIGST("DIGST", "0184", "DIGSTORG"),
    NL_OINO("NL:OINO", "0190", "NL:OINO"),
    EE_CC("EE:CC", "0191", "Estonian Company Code"),
    NO_ORG("NO:ORG", "0192", "Enhetsregisteret ved Bronnoysundregisterne"),
    DK_CPR("DK:CPR", "9901", "Danish Ministry of the Interior and Health"),
    DK_CVR("DK:CVR", "9902", "The Danish Commerce and Companies Agency"),
    DK_SE("DK:SE", "9904", "Danish Ministry of Taxation, Central Customs and Tax Administration"),
    DK_VANS("DK:VANS", "9905", "Danish VANS providers"),
    IT_VAT("IT:VAT", "9906", "Ufficio responsabile gestione partite IVA"),
    IT_CF("IT:CF", "9907", "TAX Authority"),
    NO_ORGNR("NO:ORGNR", "9908", "Enhetsregisteret ved Bronnoysundregisterne"),
    // @Deprecated
    // NO_VAT("NO:VAT", "9909", "Norwegian VAT number (Deprecated)"),
    HU_VAT("HU:VAT", "9910", "Hungarian VAT number"),
    EU_REID("EU:REID", "9913", "Business Registers Network"),
    AT_VAT("AT:VAT", "9914", "Österreichische Umsatzsteuer-Identifikationsnummer"),
    AT_GOV("AT:GOV", "9915", "Österreichisches Verwaltungs bzw. Organisationskennzeichen"),
    IS_KT("IS:KT", "9917", "Icelandic National Registry"),
    IBAN("IBAN", "9918", "SOCIETY FOR WORLDWIDE INTERBANK FINANCIAL, TELECOMMUNICATION S.W.I.F.T"),
    AT_KUR("AT:KUR", "9919", "Kennziffer des Unternehmensregisters"),
    ES_VAT("ES:VAT", "9920", "Agencia Española de Administración Tributaria"),
    IT_IPA("IT:IPA", "9921", "Indice delle Pubbliche Amministrazioni"),
    AD_VAT("AD:VAT", "9922", "Andorra VAT number"),
    AL_VAT("AL:VAT", "9923", "Albania VAT number"),
    BA_VAT("BA:VAT", "9924", "Bosnia and Herzegovina VAT number"),
    BE_VAT("BE:VAT", "9925", "Belgium VAT number"),
    BG_VAT("BG:VAT", "9926", "Bulgaria VAT number"),
    CH_VAT("CH:VAT", "9927", "Switzerland VAT number"),
    CY_VAT("CY:VAT", "9928", "Cyprus VAT number"),
    CZ_VAT("CZ:VAT", "9929", "Czech Republic VAT number"),
    DE_VAT("DE:VAT", "9930", "Germany VAT number"),
    EE_VAT("EE:VAT", "9931", "Estonia VAT number"),
    GB_VAT("GB:VAT", "9932", "United Kingdom VAT number"),
    GR_VAT("GR:VAT", "9933", "Greece VAT number"),
    HR_VAT("HR:VAT", "9934", "Croatia VAT number"),
    IE_VAT("IE:VAT", "9935", "Ireland VAT number"),
    LI_VAT("LI:VAT", "9936", "Liechtenstein VAT number"),
    LT_VAT("LT:VAT", "9937", "Lithuania VAT number"),
    LU_VAT("LU:VAT", "9938", "Luxemburg VAT number"),
    LV_VAT("LV:VAT", "9939", "Latvia VAT number"),
    MC_VAT("MC:VAT", "9940", "Monaco VAT number"),
    ME_VAT("ME:VAT", "9941", "Montenegro VAT number"),
    MK_VAT("MK:VAT", "9942", "Macedonia, the former Yugoslav Republic of VAT number"),
    MT_VAT("MT:VAT", "9943", "Malta VAT number"),
    NL_VAT("NL:VAT", "9944", "Netherlands VAT number"),
    PL_VAT("PL:VAT", "9945", "Poland VAT number"),
    PT_VAT("PT:VAT", "9946", "Portugal VAT number"),
    RO_VAT("RO:VAT", "9947", "Romania VAT number"),
    RS_VAT("RS:VAT", "9948", "Serbia VAT number"),
    SI_VAT("SI:VAT", "9949", "Slovenia VAT number"),
    SK_VAT("SK:VAT", "9950", "Slovakia VAT number"),
    SM_VAT("SM:VAT", "9951", "San Marino VAT number"),
    TR_VAT("TR:VAT", "9952", "Turkey VAT number"),
    VA_VAT("VA:VAT", "9953", "Holy See (Vatican City State) VAT number"),
    NL_ION("NL:OIN", "9954", "Dutch Originator's Identification Number"),
    SE_VAT("SE:VAT", "9955", "Swedish VAT number"),
    BE_CBE("BE:CBE", "9956", "Belgian Crossroad Bank of Enterprises"),
    FR_VAT("FR:VAT", "9957", "French VAT number");

    private static final Scheme SCHEME = Scheme.of("iso6523-actorid-upis");

    private final String identifier;

    private final String code;

    private final String issuingAgency;

    PeppolIcd(String identifier, String code, String issuingAgency) {
        this.identifier = identifier;
        this.code = code;
        this.issuingAgency = issuingAgency;
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

    @Override
    public String getIssuingAgency() {
        return issuingAgency;
    }

    public static Icd findByCode(String icd) {
        for (PeppolIcd v : values())
            if (v.code.equals(icd))
                return v;

        throw new IllegalArgumentException(String.format("Value '%s' is not valid ICD.", icd));
    }

    public static Icd findByIdentifier(String icd) {
        for (PeppolIcd v : values())
            if (v.identifier.equals(icd))
                return v;

        throw new IllegalArgumentException(String.format("Identifier '%s' is not valid ICD.", icd));
    }
}
