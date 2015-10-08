package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.security.api.SecurityException;
import no.difi.vefa.peppol.common.model.*;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import no.difi.vefa.peppol.lookup.util.DomUtils;
import org.oasis_open.docs.bdxr.ns.smp._2014._07.*;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class BdxrReader implements MetadataReader {

    public static final String NAMESPACE = "http://docs.oasis-open.org/bdxr/ns/SMP/2014/07";

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
    public List<DocumentIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse) {
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
    public ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse) throws LookupException, SecurityException{
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
                    serviceInformation.getParticipantIdentifier().getScheme()
            ));
            serviceMetadata.setDocumentIdentifier(new DocumentIdentifier(
                    serviceInformation.getDocumentIdentifier().getValue(),
                    serviceInformation.getDocumentIdentifier().getScheme()
            ));

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            for (ProcessType processType : serviceInformation.getProcessList().getProcess()) {
                ProcessIdentifier processIdentifier = new ProcessIdentifier(processType.getProcessIdentifier().getValue(), processType.getProcessIdentifier().getScheme());
                for (EndpointType endpointType : processType.getServiceEndpointList().getEndpoint()) {
                    serviceMetadata.addEndpoint(new Endpoint(
                            processIdentifier,
                            new TransportProfile(endpointType.getTransportProfile()),
                            endpointType.getEndpointURI(),
                            (X509Certificate) certificateFactory.generateCertificate(
                                    new ByteArrayInputStream(endpointType.getCertificate())
                            )
                    ));
                }
            }

            return serviceMetadata;
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (CertificateException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
