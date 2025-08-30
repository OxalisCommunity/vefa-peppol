package network.oxalis.vefa.peppol.lookup.dummy;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import network.oxalis.vefa.peppol.common.model.Endpoint;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessIdentifier;
import network.oxalis.vefa.peppol.common.model.TransportProfile;
import network.oxalis.vefa.peppol.lookup.LookupClient;
import network.oxalis.vefa.peppol.lookup.LookupClientBuilder;
import network.oxalis.vefa.peppol.mode.Mode;

class DummyLookupClientTest {

	@Test
	void testDummyLookupClient() throws Exception {
		Mode mode = Mode.of("TEST");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(LookupClientBuilder.class.getName(), LookupClientBuilder.forMode(mode));
		DummyLookupClient client = new DummyLookupClient(mode, map);

		ProcessIdentifier pi = ProcessIdentifier.of("test");

		String documentIdentifier = "busdox-docid-qns::urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1";
		ParticipantIdentifier pari = ParticipantIdentifier.of("0184:12345678");
		assertEquals(client.getDocumentIdentifiers(pari).toString(), "[" + documentIdentifier + "]");

		TransportProfile tp = TransportProfile.of("test");
		Endpoint endpoint = client.getEndpoint(null, tp);
		assertEquals(endpoint.getAddress().toString(), "http://localhost:8080/as4");
		assertEquals(endpoint.getCertificate().getSubjectX500Principal().getName(), "C=NO,O=Direktoratet for forvaltning og IKT,OU=PEPPOL PRODUCTION AP,CN=PNO000001");
		assertEquals(endpoint.getTransportProfile().toString(), "TransportProfile{test}");

		assertEquals(client.getEndpoint(null, pi, tp).getTransportProfile().toString(), "TransportProfile{test}");
		assertEquals(client.getEndpoint(null, null, pi, tp).getTransportProfile().toString(), "TransportProfile{test}");
		assertEquals(client.getServiceMetadata(null, null).getServiceInformation().getEndpoint(client.getDefaultProcessIdentifier(), client.getDefaultTransport()).getAddress().toString(), "http://localhost:8080/as4");
		assertEquals(client.getServiceReferences(pari).get(0).getDocumentTypeIdentifier().toString(), documentIdentifier);
	}

	@Test
	public void testOverridingConfiguration() throws Exception {
		assertEquals(LookupClient.class.getName(), LookupClientBuilder.forProduction().build().getClass().getName());
		assertEquals(LookupClient.class.getName(), LookupClientBuilder.forMode("TEST").build().getClass().getName());
		LookupClient dummyClient = LookupClientBuilder.forMode("DUMMY").build();
		assertEquals(DummyLookupClient.class.getName(), dummyClient.getClass().getName());
		assertEquals("http://localhost:9090/as4", ((DummyLookupClient) dummyClient).getDefaultEndpoint().getAddress().toString());
	}

}
