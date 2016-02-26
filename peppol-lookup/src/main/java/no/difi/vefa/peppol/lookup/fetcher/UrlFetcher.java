package no.difi.vefa.peppol.lookup.fetcher;

import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.LookupException;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLConnection;

public class UrlFetcher extends AbstractFetcher {

    @Override
    public FetcherResponse fetch(URI uri) throws LookupException {
        try {
            URLConnection urlConnection = uri.toURL().openConnection();
            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setReadTimeout(TIMEOUT);

            return new FetcherResponse(new BufferedInputStream(urlConnection.getInputStream()), urlConnection.getHeaderField("X-SMP-Namespace"));
        } catch (FileNotFoundException e) {
            throw new LookupException("Invalid response from SMP.", e);
        } catch (SocketTimeoutException | SocketException e) {
            throw new LookupException(String.format("Unable to fetch '%s'", uri), e);
        } catch (IOException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
