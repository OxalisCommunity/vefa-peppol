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

package no.difi.vefa.peppol.common.model;

import no.difi.vefa.peppol.common.lang.PeppolException;

import java.io.Serializable;
import java.util.regex.Pattern;

public class TransportProtocol extends AbstractSimpleIdentifier implements Serializable {

    private static final long serialVersionUID = -5938766453542971103L;

    private static Pattern pattern = Pattern.compile("[\\p{Upper}\\d]+");

    public static final TransportProtocol AS2 = new TransportProtocol("AS2");

    public static final TransportProtocol AS4 = new TransportProtocol("AS4");

    public static final TransportProtocol INTERNAL = new TransportProtocol("INTERNAL");

    public static TransportProtocol of(String value) throws PeppolException {
        if (!pattern.matcher(value).matches())
            throw new PeppolException("Identifier not according to pattern.");

        return new TransportProtocol(value);
    }

    private TransportProtocol(String identifier) {
        super(identifier);
    }

    @Override
    public String toString() {
        return "TransportProtocol{" + value + '}';
    }
}
