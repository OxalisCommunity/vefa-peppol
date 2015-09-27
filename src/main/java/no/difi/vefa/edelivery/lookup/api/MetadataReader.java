package no.difi.vefa.edelivery.lookup.api;

import no.difi.vefa.edelivery.lookup.model.DocumentIdentifier;
import no.difi.vefa.edelivery.lookup.model.ServiceMetadata;
import no.difi.vefa.edelivery.lookup.model.FetcherResponse;

import java.util.List;

public interface MetadataReader {

    List<DocumentIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse) throws LookupException;

    ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse) throws LookupException;

}
