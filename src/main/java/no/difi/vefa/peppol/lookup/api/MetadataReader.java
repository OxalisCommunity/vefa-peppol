package no.difi.vefa.peppol.lookup.api;

import no.difi.vefa.peppol.lookup.model.DocumentIdentifier;
import no.difi.vefa.peppol.lookup.model.ServiceMetadata;
import no.difi.vefa.peppol.lookup.model.FetcherResponse;

import java.util.List;

public interface MetadataReader {

    List<DocumentIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse) throws LookupException;

    ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse) throws LookupException;

}
