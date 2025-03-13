package network.oxalis.vefa.peppol.publisher.servlet;

import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.publisher.PublisherService;
import network.oxalis.vefa.peppol.publisher.lang.PublisherException;
import org.mockito.Mockito;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBException;
import java.io.OutputStream;
import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class PublisherServletTest {

    private PublisherService publisherService = mock(PublisherService.class);

    private PublisherServlet publisherServlet = new PublisherServlet(publisherService);

    private static final String PARTICIPANT_PARAM = "/iso6523-actorid-upis::9908:999888777";
    private static final String DOCUMENT_PARAM = "busdox-docid-qns::" +
            "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice" +
            "##urn:www.cenbii.eu:transaction:biitrns010:ver2.0" +
            ":extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1";

    @BeforeTest
    public void before() {
        Mockito.reset(publisherService);
    }

    @Test
    public void pathServiceGroupRegexTest() throws JAXBException, PublisherException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(PARTICIPANT_PARAM);
        HttpServletResponse response = mock(HttpServletResponse.class);

        publisherServlet.doGet(request, response);
        verify(publisherService).serviceGroup((OutputStream)isNull(), (String)isNull(), any(URI.class), any(ParticipantIdentifier.class));
    }

    @Test
    public void pathServiceMetadataRegexTest() throws JAXBException, PublisherException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(PARTICIPANT_PARAM+"/services/"+DOCUMENT_PARAM);
        HttpServletResponse response = mock(HttpServletResponse.class);

        publisherServlet.doGet(request, response);
        verify(publisherService).metadataProvider((OutputStream)isNull(), (String)isNull(), any(ParticipantIdentifier.class), any(DocumentTypeIdentifier.class));
    }
}
