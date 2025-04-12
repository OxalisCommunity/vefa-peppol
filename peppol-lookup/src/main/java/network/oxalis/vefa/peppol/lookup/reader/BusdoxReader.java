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

import com.google.common.collect.Lists;
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
import network.oxalis.peppol.busdox.jaxb.smp.*;
import org.apache.commons.codec.binary.Base64;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
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
@Namespace("http://busdox.org/serviceMetadata/publishing/1.0/")
public class BusdoxReader implements MetadataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusdoxReader.class);

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

    @Override
    public PotentiallySigned<ServiceMetadata> parseServiceMetadata(FetcherResponse fetcherResponse)
            throws LookupException, PeppolSecurityException {

        if (null == fetcherResponse)
            throw new LookupException("ServiceMetadata element not found or SMP registration is not valid.");

        try {
            Document doc = DomUtils.parse(fetcherResponse.getInputStream());

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<?> result = (JAXBElement) unmarshaller.unmarshal(new DOMSource(doc));
            Object o = result.getValue();

            X509Certificate signer = null;
            if (o instanceof SignedServiceMetadataType) {
                signer = XmldsigVerifier.verify(doc);
                o = ((SignedServiceMetadataType) o).getServiceMetadata();
            }

            if (!(o instanceof ServiceMetadataType))
                throw new LookupException("ServiceMetadata element not found.");

            ServiceMetadataType serviceMetadataType = (ServiceMetadataType) o;
            return Signed.of(getServiceMetadata(serviceMetadataType), signer);
        } catch (JAXBException | CertificateException | IOException | SAXException | ParserConfigurationException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }

    public ServiceMetadata getServiceMetadata(ServiceMetadataType serviceMetadataType) throws CertificateException, LookupException {
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

    public ServiceMetadata getServiceMetadata(ServiceInformationType serviceInformation) throws CertificateException {
        List<ProcessMetadata<Endpoint>> processMetadatas = Lists.newArrayList();
        for (ProcessType processType : serviceInformation.getProcessList().getProcess()) {
            List<Endpoint> endpoints = Lists.newArrayList();
            for (EndpointType endpointType : processType.getServiceEndpointList().getEndpoint()) {
                Period period = Period.of(
                        endpointType.getServiceActivationDate() == null ?
                                null : endpointType.getServiceActivationDate().toGregorianCalendar().getTime(),
                        endpointType.getServiceExpirationDate() == null ?
                                null : endpointType.getServiceExpirationDate().toGregorianCalendar().getTime()
                );

                endpoints.add(Endpoint.of(
                        TransportProfile.of(endpointType.getTransportProfile()),
                        URI.create(endpointType.getEndpointReference().getAddress().getValue()),
                        certificateInstance(Base64.decodeBase64(endpointType.getCertificate())),
                        period
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

        return ServiceMetadata.of(ServiceInformation.of(
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
    }

    private X509Certificate certificateInstance(byte[] content) throws CertificateException {
        return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(content));
    }
}
