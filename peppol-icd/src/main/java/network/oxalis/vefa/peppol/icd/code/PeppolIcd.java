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

package network.oxalis.vefa.peppol.icd.code;

import lombok.Getter;
import network.oxalis.vefa.peppol.common.model.Scheme;
import network.oxalis.vefa.peppol.icd.api.Icd;

import java.util.stream.Stream;

@Getter
public enum PeppolIcd implements Icd {

    // The latest code list release v9.1 dated 2025-03-03: https://docs.peppol.eu/edelivery/codelists/v9.1/Peppol%20Code%20Lists%20-%20Participant%20identifier%20schemes%20v9.1.html
    // Original Source : http://iso6523.info/icd_list.pdf

    // Internationally approved ICDs
    FR_SIRENE("FR:SIRENE", "0002", "Institut National de la Statistique et des Etudes Economiques, (I.N.S.E.E.)"),
    SE_ORGNR("SE:ORGNR", "0007", "The National Tax Board"),
    FR_SIRET("FR:SIRET", "0009", "DU PONT DE NEMOURS"),
    DUNS("DUNS", "0060", "Dun and Bradstreet Ltd"),
    GLN("GLN", "0088", "GS1 GLN"),
    DK_P("DK:P", "0096", "The Danish Business Authority"),
    IT_FTI("IT:FTI", "0097", "FTI - Ediforum Italia"),
    NL_KVK("NL:KVK", "0106", "Vereniging van Kamers van Koophandel en Fabrieken in Nederland"),
    EU_NAL("EU:NAL", "0130", "European Commission, Information Directorate, Data Transmission Service"),
    IT_SIA("IT:SIA", "0135", "SIA-Società Interbancaria per l'Automazione S.p.A."),
    IT_SECETI("IT:SECETI", "0142", "Servizi Centralizzati SECETI S.p.A."),
    AU_ABN("AU:ABN", "0151", "Australian Taxation Office"),
    CH_UIDB("CH:UIDB", "0183", "Swiss Federal Statistical Office (FSO)"),
    DK_DIGST("DK:DIGST", "0184", "The Danish Business Authority"),
    JP_SST("JP:SST", "0188", "National Tax Agency Japan"),
    NL_OINO("NL:OINO", "0190", "Logius"),
    EE_CC("EE:CC", "0191", "Centre of Registers and Information Systems of the Ministry of Justice"),
    NO_ORG("NO:ORG", "0192", "The Brønnøysund Register Centre"),
    UBLBE("UBLBE", "0193", "UBL.BE"),
    SG_UEN("SG:UEN", "0195", "lnfocomm Media Development Authority"),
    IS_KTNR("IS:KTNR", "0196", "Icelandic National Registry"),
    DK_ERST("DK:ERST", "0198", "The Danish Business Authority"),
    LEI("LEI", "0199", "Legal Entity Identifier (LEI)"),
    LT_LEC("LT:LEC", "0200", "State Enterprise Centre of Registers"),
    IT_CUUO("IT:CUUO", "0201", "Agenzia per l’Italia digitale"),
    DE_LWID("DE:LWID", "0204", "Koordinierungsstelle für IT-Standards (KoSIT)"),
    IT_COD("IT:COD", "0205", "Agenzia delle Entrate"),
    BE_EN("BE:EN", "0208", "Banque-Carrefour des Entreprises (BCE) / Kruispuntbank van Ondernemingen (KBO) / Zentrale Datenbank der Unternehmen (ZOU) Service public fédéral Economie, P.M.E. Classes moyennes et Energie"),
    GS1("GS1", "0209", "GS1"),
    IT_CFI("IT:CFI", "0210", "Agenzia delle Entrate"),
    IT_IVA("IT:IVA", "0211", "PARTITA IVA"),
    FI_OVT2("FI:OVT2", "0216", "TIEKE- Tietoyhteiskunnan kehittamiskeskus ry"),
    LV_URN("LV:URN", "0218", "The Register of Enterprises of the Republic of Latvia"),
    JP_IIN("JP:IIN", "0221", "National Tax Agency Japan"),
    FR_CTC("FR:CTC", "0225", "AIFE (Agence pour l’Informatique Financière de l’Etat)"),
    MY_EIF("MY:EIF", "0230", "Malaysia Digital Economy Corporation Sdn Bhd (MDEC)"),
    AE_TIN("AE:TIN", "0235", "UAE Federal Tax Authority"),
    LU_MAT("LU:MAT", "0240", "Centre des technologies de l'information de l'Etat (CTIE)"),
    // ICDs created and maintained by OpenPEPPOL
    @Deprecated
    NO_VAT("NO:VAT", "9909", "Enhetsregisteret ved Bronnoysundregisterne"),

    HU_VAT("HU:VAT", "9910", "Hungarian VAT number"),

    @Deprecated
    EU_VAT("EU:VAT", "9912", "National ministries of Economy"),

    EU_REID("EU:REID", "9913", "Business Registers Network"),
    AT_VAT("AT:VAT", "9914", "Österreichische Umsatzsteuer-Identifikationsnummer"),
    AT_GOV("AT:GOV", "9915", "Österreichisches Verwaltungs bzw. Organisationskennzeichen"),

    @Deprecated
    AT_CID("AT:CID", "9916", "Firmenidentifikationsnummer der Statistik Austria"),
    @Deprecated
    IS_KT("IS:KT", "9917", "Icelandic National Registry"),

    IBAN("IBAN", "9918", "SOCIETY FOR WORLDWIDE INTERBANK FINANCIAL, TELECOMMUNICATION S.W.I.F.T"),
    AT_KUR("AT:KUR", "9919", "Kennziffer des Unternehmensregisters"),
    ES_VAT("ES:VAT", "9920", "Agencia Española de Administración Tributaria"),
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

    @Deprecated
    NL_ION("NL:OIN", "9954", "Dutch Originator's Identification Number"),

    FR_VAT("FR:VAT", "9957", "French VAT number"),
    US_EIN("US:EIN", "9959", "US Employer ID Number"),
    ;

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
    public Scheme getScheme() {
        return SCHEME;
    }

    public static Icd findByCode(String icd) {
        return Stream.of(values())
                .filter(v -> v.code.equals(icd))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Value '%s' is not valid ICD.", icd)));
    }

    public static Icd findByIdentifier(String icd) {
        for (PeppolIcd v : values())
            if (v.identifier.equals(icd))
                return v;

        throw new IllegalArgumentException(String.format("Identifier '%s' is not valid ICD.", icd));
    }
}
