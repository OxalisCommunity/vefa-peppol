package no.difi.vefa.edelivery.lookup.api;


import no.difi.vefa.edelivery.lookup.model.FetcherResponse;

import java.net.URI;

public interface MetadataFetcher {

    FetcherResponse fetch(URI uri) throws LookupException;

}
