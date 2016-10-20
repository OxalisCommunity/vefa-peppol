/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
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

package no.difi.vefa.peppol.security.mode;

import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.security.Mode;

import java.io.InputStream;

public class ProductionMode extends AbstractPeppolMode {

    private static String[] issuersAp = new String[]{"PEPPOL ACCESS POINT CA"};

    private static String[] issuersSmp = new String[]{"PEPPOL SERVICE METADATA PUBLISHER CA"};

    @Override
    public String getIdentifier() {
        return Mode.PRODUCTION;
    }

    @Override
    protected String[] getIssuersInternal(Service service) {
        if (Service.AP.equals(service))
            return issuersAp;
        else if (Service.SMP.equals(service))
            return issuersSmp;
        else
            return new String[0];
    }

    @Override
    public InputStream getKeystore() {
        return getClass().getResourceAsStream("/peppol-production.jks");
    }
}
