package no.difi.vefa.peppol.lookup.fetcher;

import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataFetcher;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;

public class UrlFetcher implements MetadataFetcher {

    @Override
    public FetcherResponse fetch(URI uri) throws LookupException {
        try {
            URLConnection urlConnection = uri.toURL().openConnection();
            return new FetcherResponse(new BufferedInputStream(urlConnection.getInputStream()), urlConnection.getHeaderField("X-SMP-Namespace"));
        } catch (FileNotFoundException e) {
            throw new LookupException("Invalid response from SMP.", e);
        } catch (IOException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
