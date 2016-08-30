package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.common.util.DomUtils;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.security.api.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.apache.commons.codec.binary.Base64;
import org.busdox.servicemetadata.publishing._1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class BusdoxReader implements MetadataReader {

    private static Logger logger = LoggerFactory.getLogger(BusdoxReader.class);

    public static final String NAMESPACE = "http://busdox.org/serviceMetadata/publishing/1.0/";

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(ServiceGroupType.class, SignedServiceMetadataType.class,
                    ServiceMetadataType.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentTypeIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse)
            throws LookupException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ServiceGroupType serviceGroup = unmarshaller.unmarshal(new StreamSource(fetcherResponse.getInputStream()), ServiceGroupType.class).getValue();
            List<DocumentTypeIdentifier> documentTypeIdentifiers = new ArrayList<>();

            for (ServiceMetadataReferenceType reference : serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReference()) {
                String hrefDocumentTypeIdentifier = URLDecoder.decode(reference.getHref().split("/services/")[1], "UTF-8");
                String[] parts = hrefDocumentTypeIdentifier.split("::", 2);

                try {
                    documentTypeIdentifiers.add(new DocumentTypeIdentifier(parts[1], new Scheme(parts[0]), URI.create(reference.getHref())));
                } catch (ArrayIndexOutOfBoundsException e) {
                    logger.warn("Unable to parse '{}'.", hrefDocumentTypeIdentifier);
                }
            }

            return documentTypeIdentifiers;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse)
            throws LookupException, PeppolSecurityException {
        try {
            Document doc = DomUtils.parse(fetcherResponse.getInputStream());

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<?> result = (JAXBElement) unmarshaller.unmarshal(new DOMSource(doc));
            Object o = result.getValue();

            ServiceMetadata serviceMetadata = new ServiceMetadata();

            if (o instanceof SignedServiceMetadataType) {
                serviceMetadata.setSigner(XmldsigVerifier.verify(doc));
                o = ((SignedServiceMetadataType) o).getServiceMetadata();
            }

            if (!(o instanceof ServiceMetadataType))
                throw new LookupException("ServiceMetadata element not found.");

            ServiceInformationType serviceInformation = ((ServiceMetadataType) o).getServiceInformation();
            serviceMetadata.setParticipantIdentifier(new ParticipantIdentifier(
                    serviceInformation.getParticipantIdentifier().getValue(),
                    new Scheme(serviceInformation.getParticipantIdentifier().getScheme())
            ));
            serviceMetadata.setDocumentTypeIdentifier(new DocumentTypeIdentifier(
                    serviceInformation.getDocumentIdentifier().getValue(),
                    new Scheme(serviceInformation.getDocumentIdentifier().getScheme())
            ));

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            for (ProcessType processType : serviceInformation.getProcessList().getProcess()) {
                ProcessIdentifier processIdentifier = new ProcessIdentifier(
                        processType.getProcessIdentifier().getValue(),
                        new Scheme(processType.getProcessIdentifier().getScheme())
                );

                for (EndpointType endpointType : processType.getServiceEndpointList().getEndpoint()) {
                    serviceMetadata.addEndpoint(new Endpoint(
                            processIdentifier,
                            new TransportProfile(endpointType.getTransportProfile()),
                            endpointType.getEndpointReference().getAddress().getValue(),
                            (X509Certificate) certificateFactory.generateCertificate(
                                    new ByteArrayInputStream(Base64.decodeBase64(endpointType.getCertificate()))
                            )
                    ));
                }
            }

            return serviceMetadata;
        } catch (JAXBException | CertificateException | IOException | SAXException | ParserConfigurationException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
