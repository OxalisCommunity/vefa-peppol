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

package no.difi.vefa.peppol.sbdh;

import javax.xml.namespace.QName;

public class Ns {

    public static final String SBDH = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader";

    public static final String EXTENSION = "http://peppol.eu/xsd/ticc/envelope/1.0";

    public static final QName QNAME_BINARY_CONTENT = new QName(EXTENSION, "BinaryContent");

    public static final QName QNAME_TEXT_CONTENT = new QName(EXTENSION, "TextContent");

    public static final QName QNAME_SBD = new QName(SBDH, "StandardBusinessDocument");

    public static final QName QNAME_SBDH = new QName(SBDH, "StandardBusinessDocumentHeader");

}
