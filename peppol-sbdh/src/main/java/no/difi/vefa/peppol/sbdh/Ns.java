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

package no.difi.vefa.peppol.sbdh;

import javax.xml.namespace.QName;

public class Ns {

    public static final String SBDH = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader";

    public static final String EXTENSION = "http://peppol.eu/xsd/ticc/envelope/1.0";

    public static final QName QNAME_BINARY_CONTENT = new QName(EXTENSION, "BinaryContent");

    public static final QName QNAME_SBD = new QName(SBDH, "StandardBusinessDocument");

    public static final QName QNAME_SBDH = new QName(SBDH, "StandardBusinessDocumentHeader");

}
