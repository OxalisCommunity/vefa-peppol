package network.oxalis.vefa.peppol.lookup.dummy;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bouncycastle.util.encoders.Base64;

import network.oxalis.vefa.peppol.common.lang.EndpointNotFoundException;
import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.Endpoint;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessMetadata;
import network.oxalis.vefa.peppol.common.model.ServiceInformation;
import network.oxalis.vefa.peppol.common.model.ServiceMetadata;
import network.oxalis.vefa.peppol.common.model.ServiceReference;
import network.oxalis.vefa.peppol.common.model.TransportProfile;
import network.oxalis.vefa.peppol.lookup.LookupClient;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.mode.Mode;
import network.oxalis.vefa.peppol.security.lang.PeppolSecurityException;

/**
 * Dummy implementation of LookupClient, which can be configured via reference.conf as:
 * 
 * <pre>
 
mode.TEST.lookup.impl.class = network.oxalis.vefa.peppol.lookup.dummy.DummyLookupClient

mode.TEST.lookup.impl.dummy = {
	uri: "http://localhost:8080/as4"
	certificate: "MIIGkTCCBMWgAwIBA...+jUH1N7v7JJdaA="
	transport: "peppol-transport-as4-v2_0"
	process: "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"
	doctype: "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"
}
 * </pre>
 * 
 * It can be used for testing the sending process by just imitating all lookups to the same URL and profile.
 */
public class DummyLookupClient extends LookupClient {

	private URI uri;
	private X509Certificate certificate;
	private TransportProfile transport;
	private ProcessIdentifier processIdentifier;
	private DocumentTypeIdentifier docType;

	private Endpoint endpoint;

	public DummyLookupClient(Mode mode, Map<String, Object> objectStorage) {
		super(mode, objectStorage);
		init(mode);
	}

	private void init(Mode mode) {
		String prefix = "lookup.impl.dummy.";

		String uri = mode.getString(prefix + "uri");
		String certificateBase64 = mode.getString(prefix + "certificate");
		String transportCode = mode.getString(prefix + "transport");
		String process = mode.getString(prefix + "process");
		String doctype = mode.getString(prefix + "doctype");

		try {
			this.uri = new URI(uri);
		} catch (Exception e1) {
			throw new RuntimeException("Cannot build URI by " + uri, e1);
		}
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			this.certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decode(certificateBase64)));
		} catch (Exception e1) {
			throw new RuntimeException("Cannot build dummy X.509 certificate by the base64 value " + certificateBase64, e1);
		}

		this.transport = TransportProfile.of(transportCode);
		this.endpoint = new Endpoint(this.transport, this.uri, this.certificate, null);
		this.processIdentifier = ProcessIdentifier.of(process);
		this.docType = DocumentTypeIdentifier.of(doctype);
	}

	@Override
	public List<ServiceReference> getServiceReferences(ParticipantIdentifier participantIdentifier) throws LookupException {
		return Arrays.asList(ServiceReference.of(docType, this.processIdentifier));
	}

	@Override
	public List<DocumentTypeIdentifier> getDocumentIdentifiers(ParticipantIdentifier participantIdentifier) throws LookupException {
		return Arrays.asList(this.docType);
	}

	@Override
	public ServiceMetadata getServiceMetadata(ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier) throws LookupException, PeppolSecurityException {
		List<ProcessMetadata<Endpoint>> processes = Arrays.asList(ProcessMetadata.of(this.processIdentifier, this.endpoint));
		ServiceInformation<Endpoint> serviceInformation = ServiceInformation.of(participantIdentifier, documentTypeIdentifier, processes);
		return ServiceMetadata.of(serviceInformation);
	}

	@Override
	public Endpoint getEndpoint(ServiceMetadata serviceMetadata, ProcessIdentifier processIdentifier, TransportProfile... transportProfiles) throws PeppolSecurityException, EndpointNotFoundException {
		return Endpoint.of(getFirstOrDefault(transportProfiles), uri, certificate);
	}

	@Override
	public Endpoint getEndpoint(ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier, ProcessIdentifier processIdentifier, TransportProfile... transportProfiles) throws LookupException, PeppolSecurityException, EndpointNotFoundException {
		return Endpoint.of(getFirstOrDefault(transportProfiles), uri, certificate);
	}

	@Override
	public Endpoint getEndpoint(Header header, TransportProfile... transportProfiles) throws LookupException, PeppolSecurityException, EndpointNotFoundException {
		return Endpoint.of(getFirstOrDefault(transportProfiles), uri, certificate);
	}

	private TransportProfile getFirstOrDefault(TransportProfile... transportProfiles) {
		return transportProfiles != null && transportProfiles.length > 0 ? transportProfiles[0] : this.transport;
	}

	public URI getDefaultUri() {
		return uri;
	}

	public X509Certificate getDefaultCertificate() {
		return certificate;
	}

	public TransportProfile getDefaultTransport() {
		return transport;
	}

	public ProcessIdentifier getDefaultProcessIdentifier() {
		return processIdentifier;
	}

	public DocumentTypeIdentifier getDefaultDocType() {
		return docType;
	}

	public Endpoint getDefaultEndpoint() {
		return endpoint;
	}

}
