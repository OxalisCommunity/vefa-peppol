package no.difi.vefa.edelivery.lookup.fetcher;

import no.difi.vefa.edelivery.lookup.api.LookupException;
import no.difi.vefa.edelivery.lookup.api.MetadataFetcher;
import no.difi.vefa.edelivery.lookup.model.FetcherResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.net.URI;

public class ApacheFetcher implements MetadataFetcher {

    private static Logger logger = LoggerFactory.getLogger(ApacheFetcher.class);

    private HttpClient httpClient;

    public ApacheFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ApacheFetcher() {
        this(HttpClients.createDefault());
    }

    @Override
    public FetcherResponse fetch(URI uri) throws LookupException {
        try {
            logger.debug("{}", uri);
            HttpResponse response = httpClient.execute(new HttpGet(uri));

            switch (response.getStatusLine().getStatusCode()) {
                case 200:
                    return new FetcherResponse(
                            new BufferedInputStream(response.getEntity().getContent()),
                            response.containsHeader("X-SMP-Namespace") ? response.getFirstHeader("X-SMP-Namespace").getValue() : null
                    );
                case 404:
                    throw new LookupException("Not supported.");
                default:
                    throw new LookupException(String.format("Received code %s for lookup.", response.getStatusLine().getStatusCode()));
            }
        } catch (Exception e) {
            throw new LookupException(e);
        }
    }
}
