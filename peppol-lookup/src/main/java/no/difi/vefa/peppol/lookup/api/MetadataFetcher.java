package no.difi.vefa.peppol.lookup.api;


import java.net.URI;

public interface MetadataFetcher {

    FetcherResponse fetch(URI uri) throws LookupException;

}
