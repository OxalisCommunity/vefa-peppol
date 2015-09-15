package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.lookup.model.DocumentIdentifier;
import no.difi.vefa.peppol.lookup.model.FetcherResponse;
import no.difi.vefa.peppol.lookup.model.ServiceMetadata;

import java.io.InputStream;
import java.util.List;

/**
 * Currently not implemented.
 */
public class BdxrReader implements MetadataReader {

    public static final String NAMESPACE = "http://docs.oasis-open.org/bdxr/ns/SMP/2014/07";

    @Override
    public List<DocumentIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse) {
        return null;
    }

    @Override
    public ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse) {
        return null;
    }
}
