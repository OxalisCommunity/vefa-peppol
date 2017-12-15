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
            if (v.getCode().equals(code) && v.getScheme().equals(scheme))
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
