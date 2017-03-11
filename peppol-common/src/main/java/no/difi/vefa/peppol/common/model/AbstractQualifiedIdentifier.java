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

package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.api.QualifiedIdentifier;
import no.difi.vefa.peppol.common.util.ModelUtils;

/**
 * @author erlend
 */
public abstract class AbstractQualifiedIdentifier implements QualifiedIdentifier {

    protected Scheme scheme;

    protected String identifier;

    public AbstractQualifiedIdentifier(String identifier, Scheme scheme) {
        this.scheme = scheme;
        this.identifier = identifier;
    }

    @Override
    public Scheme getScheme() {
        return scheme;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String urlencoded() {
        return ModelUtils.urlencode("%s::%s", scheme.getValue(), identifier);
    }

}
