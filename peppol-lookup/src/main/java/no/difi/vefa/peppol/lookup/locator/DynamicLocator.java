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

package no.difi.vefa.peppol.lookup.locator;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;

import java.net.URI;

public class DynamicLocator extends AbstractLocator {

    public static final String OPENPEPPOL_PRODUCTION = "edelivery.tech.ec.europa.eu";

    public static final String OPENPEPPOL_TEST = "acc.edelivery.tech.ec.europa.eu";

    private MetadataLocator locator;

    public DynamicLocator(String hostname) {
        locator = OPENPEPPOL_TEST.equals(hostname) ? new BdxlLocator(hostname) : new BusdoxLocator(hostname);
    }

    @Override
    public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        return locator.lookup(participantIdentifier);
    }
}
