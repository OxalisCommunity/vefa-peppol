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

package no.difi.vefa.peppol.lookup.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);

    private static final Pattern ROOT_TAG_PATTERN =
            Pattern.compile("<(\\w*:{0,1}[^<?|^<!]*)>", Pattern.MULTILINE);

    private static final Pattern NAMESPACE_PATTERN =
            Pattern.compile("xmlns:{0,1}([A-Za-z0-9]*)\\w*=\\w*\"(.+?)\"", Pattern.MULTILINE);

    private static XMLInputFactory XML_INPUT_FACTORY;

    static {
        XML_INPUT_FACTORY = XMLInputFactory.newFactory();
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XML_INPUT_FACTORY.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
    }

    public static String extractRootNamespace(String xmlContent) {
        Matcher matcher = ROOT_TAG_PATTERN.matcher(xmlContent);
        if (matcher.find()) {
            String rootElement = matcher.group(1).trim();
            LOGGER.debug("Root element: {}", rootElement);
            String rootNs = rootElement.split(" ", 2)[0].contains(":") ?
                    rootElement.substring(0, rootElement.indexOf(":")) : "";
            LOGGER.debug("Namespace: {}", rootNs);

            Matcher nsMatcher = NAMESPACE_PATTERN.matcher(rootElement);
            while (nsMatcher.find()) {
                LOGGER.debug(nsMatcher.group(0));

                if (nsMatcher.group(1).equals(rootNs)) {
                    return nsMatcher.group(2);
                }
            }
        }

        return null;
    }

    public static XMLStreamReader streamReader(InputStream inputStream) throws XMLStreamException {
        return XML_INPUT_FACTORY.createXMLStreamReader(inputStream);
    }

    XmlUtils() {

    }
}
