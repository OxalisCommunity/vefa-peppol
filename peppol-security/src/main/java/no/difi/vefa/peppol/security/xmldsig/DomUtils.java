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

package no.difi.vefa.peppol.security.xmldsig;

import no.difi.vefa.peppol.common.api.PerformResult;
import no.difi.vefa.peppol.common.util.ExceptionUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class DomUtils {

    public static final DocumentBuilderFactory documentBuilderFactory;

    static {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
    }

    public static Document parse(InputStream inputStream)
            throws SAXException, IOException, ParserConfigurationException {
        return documentBuilderFactory.newDocumentBuilder().parse(inputStream);
    }

    public static DocumentBuilder newDocumentBuilder() {
        return ExceptionUtil.perform(IllegalStateException.class, new PerformResult<DocumentBuilder>() {
            @Override
            public DocumentBuilder action() throws Exception {
                return documentBuilderFactory.newDocumentBuilder();
            }
        });
    }
}
