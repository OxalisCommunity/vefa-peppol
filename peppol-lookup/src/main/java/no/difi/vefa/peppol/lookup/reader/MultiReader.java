package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ServiceMetadata;
import no.difi.vefa.peppol.common.util.XmlUtils;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.security.api.PeppolSecurityException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.List;

public class MultiReader implements MetadataReader {

    private BusdoxReader busdoxReader = new BusdoxReader();
    private BdxrReader bdxrReader = new BdxrReader();

    @Override
    public List<DocumentTypeIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse) throws LookupException {
        if (fetcherResponse.getNamespace() == null)
            fetcherResponse = detect(fetcherResponse);

        if (BusdoxReader.NAMESPACE.equalsIgnoreCase(fetcherResponse.getNamespace()))
            return busdoxReader.parseDocumentIdentifiers(fetcherResponse);
        else if (BdxrReader.NAMESPACE.equalsIgnoreCase(fetcherResponse.getNamespace()))
            return bdxrReader.parseDocumentIdentifiers(fetcherResponse);

        throw new LookupException(String.format("Unknown namespace: %s", fetcherResponse.getNamespace()));
    }

    @Override
    public ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse) throws LookupException, PeppolSecurityException {
        if (fetcherResponse.getNamespace() == null)
            fetcherResponse = detect(fetcherResponse);

        if (BusdoxReader.NAMESPACE.equalsIgnoreCase(fetcherResponse.getNamespace()))
            return busdoxReader.parseServiceMetadata(fetcherResponse);
        else if (BdxrReader.NAMESPACE.equalsIgnoreCase(fetcherResponse.getNamespace()))
            return bdxrReader.parseServiceMetadata(fetcherResponse);

        throw new LookupException(String.format("Unknown namespace: %s", fetcherResponse.getNamespace()));
    }

    public FetcherResponse detect(FetcherResponse fetcherResponse) throws LookupException {
        try {
            byte[] fileContent = new byte[1024];
            int size = fetcherResponse.getInputStream().read(fileContent);
            fileContent = Arrays.copyOfRange(fileContent, 0, size);

            String namespace = XmlUtils.extractRootNamespace(new String(fileContent));
            if (namespace != null) {
                return new FetcherResponse(
                        new SequenceInputStream(new ByteArrayInputStream(fileContent), fetcherResponse.getInputStream()),
                        namespace
                );
            }

            throw new LookupException("Unable to detect namespace.");
        } catch (IOException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
