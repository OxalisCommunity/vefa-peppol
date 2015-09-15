package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.lookup.model.DocumentIdentifier;
import no.difi.vefa.peppol.lookup.model.FetcherResponse;
import no.difi.vefa.peppol.lookup.model.ServiceMetadata;

import java.util.List;

public class MultiReader implements MetadataReader {

    private BusdoxReader busdoxReader = new BusdoxReader();
    private BdxrReader bdxrReader = new BdxrReader();

    @Override
    public List<DocumentIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse) throws LookupException {
        if (BusdoxReader.NAMESPACE.equals(fetcherResponse.getNamespace()))
            return busdoxReader.parseDocumentIdentifiers(fetcherResponse);
        else if (BdxrReader.NAMESPACE.equals(fetcherResponse.getNamespace()))
            return bdxrReader.parseDocumentIdentifiers(fetcherResponse);
        else
            return busdoxReader.parseDocumentIdentifiers(fetcherResponse);
    }

    @Override
    public ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse) throws LookupException {
        if (BusdoxReader.NAMESPACE.equals(fetcherResponse.getNamespace()))
            return busdoxReader.parseServiceMetadata(fetcherResponse);
        else if (BdxrReader.NAMESPACE.equals(fetcherResponse.getNamespace()))
            return bdxrReader.parseServiceMetadata(fetcherResponse);
        else
            return busdoxReader.parseServiceMetadata(fetcherResponse);
    }
}
