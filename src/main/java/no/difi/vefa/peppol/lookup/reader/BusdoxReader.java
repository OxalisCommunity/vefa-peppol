package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.lookup.model.*;
import org.apache.commons.codec.binary.Base64;
import org.busdox.servicemetadata.publishing._1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
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
            jaxbContext = JAXBContext.newInstance(ServiceGroupType.class, SignedServiceMetadataType.class, ServiceMetadataType.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse) throws LookupException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ServiceGroupType serviceGroup = ((JAXBElement<ServiceGroupType>) unmarshaller.unmarshal(fetcherResponse.getInputStream())).getValue();
            List<DocumentIdentifier> documentIdentifiers = new ArrayList<DocumentIdentifier>();

            for (ServiceMetadataReferenceType reference : serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReference()) {
                String[] parts = URLDecoder.decode(reference.getHref().split("/services/")[1], "UTF-8").split("::", 2);
                documentIdentifiers.add(new DocumentIdentifier(parts[1], parts[0]));
            }

            return documentIdentifiers;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse) throws LookupException{
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<?> result = (JAXBElement) unmarshaller.unmarshal(fetcherResponse.getInputStream());
            Object o = result.getValue();

            if (o instanceof SignedServiceMetadataType) {
                SignedServiceMetadataType signedServiceMetadataType = (SignedServiceMetadataType) o;
                // TODO Verify signature
                // TODO Fetch signer
                o = signedServiceMetadataType.getServiceMetadata();
            }

            if (!(o instanceof ServiceMetadataType))
                throw new LookupException("ServiceMetadata element not found.");

            ServiceMetadata serviceMetadata = new ServiceMetadata();

            ServiceInformationType serviceInformation = ((ServiceMetadataType) o).getServiceInformation();
            serviceMetadata.setParticipantIdentifier(new ParticipantIdentifier(
                    serviceInformation.getParticipantIdentifier().getValue(),
                    serviceInformation.getParticipantIdentifier().getScheme()
            ));
            serviceMetadata.setDocumentIdentifier(new DocumentIdentifier(
                    serviceInformation.getDocumentIdentifier().getValue(),
                    serviceInformation.getDocumentIdentifier().getScheme()
            ));

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            for (EndpointType endpointType : serviceInformation.getProcessList().getProcess().get(0).getServiceEndpointList().getEndpoint()) {
                serviceMetadata.addEndpoint(new Endpoint(
                        endpointType.getTransportProfile(),
                        endpointType.getEndpointReference().getAddress().getValue(),
                        (X509Certificate) certificateFactory.generateCertificate(
                                new ByteArrayInputStream(Base64.decodeBase64(endpointType.getCertificate()))
                        )
                ));

                logger.debug(serviceMetadata.getEndpoints().get(0).getCertificate().getIssuerDN().toString());
                logger.debug(serviceMetadata.getEndpoints().get(0).getCertificate().getSubjectDN().toString());
            }

            return serviceMetadata;
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (CertificateException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
