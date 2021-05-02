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

package network.oxalis.vefa.peppol.lookup.reader;

import network.oxalis.vefa.peppol.common.api.PotentiallySigned;
import network.oxalis.vefa.peppol.common.lang.PeppolRuntimeException;
import network.oxalis.vefa.peppol.common.model.*;
import network.oxalis.vefa.peppol.common.util.ExceptionUtil;
import network.oxalis.vefa.peppol.lookup.api.FetcherResponse;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.MetadataReader;
import network.oxalis.vefa.peppol.lookup.api.Namespace;
import network.oxalis.vefa.peppol.lookup.model.DocumentTypeIdentifierWithUri;
import network.oxalis.vefa.peppol.lookup.util.XmlUtils;
import network.oxalis.vefa.peppol.security.lang.PeppolSecurityException;
import network.oxalis.vefa.peppol.security.xmldsig.DomUtils;
import network.oxalis.vefa.peppol.security.xmldsig.XmldsigVerifier;
import no.difi.commons.bdx.jaxb.smp._2016._05.*;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@MetaInfServices
@Namespace("http://docs.oasis-open.org/bdxr/ns/SMP/2016/05")
public class Bdxr201605Reader implements MetadataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bdxr201605Reader.class);

    private static JAXBContext jaxbContext;

    private static CertificateFactory certificateFactory;

    static {
        ExceptionUtil.perform(PeppolRuntimeException.class, () -> {
            jaxbContext = JAXBContext.newInstance(ServiceGroupType.class, SignedServiceMetadataType.class,
                    ServiceMetadataType.class);
            certificateFactory = CertificateFactory.getInstance("X.509");
        });
    }

    @SuppressWarnings("all")
    @Override
    public List<ServiceReference> parseServiceGroup(FetcherResponse fetcherResponse) throws LookupException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ServiceGroupType serviceGroup = unmarshaller.unmarshal(
                    XmlUtils.streamReader(fetcherResponse.getInputStream()), ServiceGroupType.class).getValue();
            List<ServiceReference> serviceReferences = new ArrayList<>();

            for (ServiceMetadataReferenceType reference :
                    serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReference()) {
                String hrefDocumentTypeIdentifier =
                        URLDecoder.decode(reference.getHref(), "UTF-8").split("/services/")[1];
                String[] parts = hrefDocumentTypeIdentifier.split("::", 2);

                try {
                    serviceReferences.add(ServiceReference.of(DocumentTypeIdentifierWithUri.of(
                            parts[1], Scheme.of(parts[0]), URI.create(reference.getHref()))));
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOGGER.warn("Unable to parse '{}'.", hrefDocumentTypeIdentifier);
                }
            }

            return serviceReferences;
        } catch (JAXBException | XMLStreamException | UnsupportedEncodingException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("all")
    @Override
    public PotentiallySigned<ServiceMetadata> parseServiceMetadata(FetcherResponse fetcherResponse)
            throws LookupException, PeppolSecurityException {
        try {
            Document doc = DomUtils.parse(fetcherResponse.getInputStream());

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            Object o = ((JAXBElement<?>) unmarshaller.unmarshal(new DOMSource(doc))).getValue();

            X509Certificate signer = null;
            if (o instanceof SignedServiceMetadataType) {
                signer = XmldsigVerifier.verify(doc);
                o = ((SignedServiceMetadataType) o).getServiceMetadata();
            }

            ServiceMetadataType serviceMetadataType = (ServiceMetadataType) o;
            ServiceMetadata serviceMetadata = getServiceMetadata(serviceMetadataType);
            return Signed.of(serviceMetadata, signer);
        } catch (JAXBException | CertificateException | IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private ServiceMetadata getServiceMetadata(ServiceMetadataType serviceMetadataType) throws CertificateException, LookupException {
        ServiceInformationType serviceInformation = serviceMetadataType.getServiceInformation();

        if (serviceInformation != null) {
            return getServiceMetadata(serviceInformation);
        }

        RedirectType redirect = serviceMetadataType.getRedirect();

        if (redirect != null) {
            return ServiceMetadata.of(Redirect.of(redirect.getCertificateUID(), redirect.getHref()));
        }

        throw new LookupException("Expected one of ServiceInformationType or RedirectType");
    }

    private ServiceMetadata getServiceMetadata(ServiceInformationType serviceInformation) throws CertificateException {
        List<ProcessMetadata<Endpoint>> processMetadatas = new ArrayList<>();
        for (ProcessType processType : serviceInformation.getProcessList().getProcess()) {
            List<Endpoint> endpoints = new ArrayList<>();
            for (EndpointType endpointType : processType.getServiceEndpointList().getEndpoint()) {
                endpoints.add(Endpoint.of(
                        TransportProfile.of(endpointType.getTransportProfile()),
                        URI.create(endpointType.getEndpointURI()),
                        certificateInstance(endpointType.getCertificate())
                ));
            }

            processMetadatas.add(ProcessMetadata.of(
                    ProcessIdentifier.of(
                            processType.getProcessIdentifier().getValue(),
                            Scheme.of(processType.getProcessIdentifier().getScheme())
                    ),
                    endpoints
            ));
        }

        ServiceMetadata serviceMetadata = ServiceMetadata.of(ServiceInformation.of(
                ParticipantIdentifier.of(
                        serviceInformation.getParticipantIdentifier().getValue(),
                        Scheme.of(serviceInformation.getParticipantIdentifier().getScheme())
                ),
                DocumentTypeIdentifier.of(
                        serviceInformation.getDocumentIdentifier().getValue(),
                        Scheme.of(serviceInformation.getDocumentIdentifier().getScheme())
                ),
                processMetadatas
        ));
        return serviceMetadata;
    }

    private X509Certificate certificateInstance(byte[] content) throws CertificateException {
        return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(content));
    }
}
