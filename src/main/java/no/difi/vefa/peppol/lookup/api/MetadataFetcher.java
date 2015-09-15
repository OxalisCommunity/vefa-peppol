package no.difi.vefa.peppol.lookup.api;

import no.difi.vefa.peppol.lookup.model.FetcherResponse;

import java.io.InputStream;
import java.net.URI;

public interface MetadataFetcher {

    FetcherResponse fetch(URI uri) throws LookupException;

}
