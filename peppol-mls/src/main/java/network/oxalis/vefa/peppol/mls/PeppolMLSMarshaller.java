/*
 * Copyright 2015-2026 Direktoratet for forvaltning og IKT
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

package network.oxalis.vefa.peppol.mls;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import network.oxalis.peppol.ubl2.jaxb.ApplicationResponseType;
import network.oxalis.peppol.ubl2.jaxb.ObjectFactory;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public final class PeppolMLSMarshaller {

    private static final JAXBContext CONTEXT;

    static {
        try {
            CONTEXT = JAXBContext.newInstance(ApplicationResponseType.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JAXBContext", e);
        }
    }

    private PeppolMLSMarshaller() {
    }

    public static byte[] marshal(ApplicationResponseType mls) {

        try {
            PeppolMLSIntegrityValidator.validate(mls);

            Marshaller marshaller = CONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ObjectFactory factory = new ObjectFactory();
            JAXBElement<ApplicationResponseType> root =
                    factory.createApplicationResponse(mls);

            marshaller.marshal(root, out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to marshal MLS", e);
        }
    }

    public static ApplicationResponseType unmarshal(String mlsXML) {

        try {
            Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();

            JAXBElement<ApplicationResponseType> root =
                    unmarshaller.unmarshal(
                            new javax.xml.transform.stream.StreamSource(
                                    new java.io.StringReader(mlsXML)),
                            ApplicationResponseType.class
                    );

            return root.getValue();

        } catch (Exception e) {
            throw new RuntimeException("Failed to unmarshal MLS", e);
        }
    }

    public static ApplicationResponseType unmarshal(byte[] xml) {
        return unmarshal(new String(xml, StandardCharsets.UTF_8));
    }

    public static String marshalToString(ApplicationResponseType mls) {
        return new String(marshal(mls), StandardCharsets.UTF_8);
    }
}
