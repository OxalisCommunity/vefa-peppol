package no.difi.vefa.edelivery.lookup.reader;

import no.difi.vefa.edelivery.lookup.api.*;
import no.difi.vefa.edelivery.lookup.api.SecurityException;
import no.difi.vefa.edelivery.lookup.model.DocumentIdentifier;
import no.difi.vefa.edelivery.lookup.model.FetcherResponse;
import no.difi.vefa.edelivery.lookup.model.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiReader implements MetadataReader {

    private static Logger logger = LoggerFactory.getLogger(MultiReader.class);

    private static final Pattern rootTagPattern = Pattern.compile("<(\\w*:{0,1}[^<?]*)>", Pattern.MULTILINE);
    private static final Pattern namespacePattern = Pattern.compile("xmlns:{0,1}([a-z0-9]*)\\w*=\\w*\"(.+?)\"", Pattern.MULTILINE);

    private BusdoxReader busdoxReader = new BusdoxReader();
    private BdxrReader bdxrReader = new BdxrReader();

    @Override
    public List<DocumentIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse) throws LookupException {
        if (fetcherResponse.getNamespace() == null)
            fetcherResponse = detect(fetcherResponse);

        if (BusdoxReader.NAMESPACE.equalsIgnoreCase(fetcherResponse.getNamespace()))
            return busdoxReader.parseDocumentIdentifiers(fetcherResponse);
        else if (BdxrReader.NAMESPACE.equalsIgnoreCase(fetcherResponse.getNamespace()))
            return bdxrReader.parseDocumentIdentifiers(fetcherResponse);

        throw new LookupException(String.format("Unknown namespace: %s", fetcherResponse.getNamespace()));
    }

    @Override
    public ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse) throws LookupException, SecurityException {
        if (fetcherResponse.getNamespace() == null)
            fetcherResponse = detect(fetcherResponse);

        if (BusdoxReader.NAMESPACE.equalsIgnoreCase(fetcherResponse.getNamespace()))
            return busdoxReader.parseServiceMetadata(fetcherResponse);
        else if (BdxrReader.NAMESPACE.equalsIgnoreCase(fetcherResponse.getNamespace()))
            return bdxrReader.parseServiceMetadata(fetcherResponse);

        throw new LookupException(String.format("Unknown namespace: %s", fetcherResponse.getNamespace()));
    }

    @SuppressWarnings("all")
    public FetcherResponse detect(FetcherResponse fetcherResponse) throws LookupException {
        try {
            byte[] fileContent = new byte[1024];
            fetcherResponse.getInputStream().read(fileContent);

            Matcher matcher = rootTagPattern.matcher(new String(fileContent));
            if (matcher.find()) {
                String rootElement = matcher.group(1).trim();
                logger.debug("Root element: {}", rootElement);
                String rootNs = rootElement.split(" ", 2)[0].contains(":") ? rootElement.substring(0, rootElement.indexOf(":")) : "";
                logger.debug("Namespace: {}", rootNs);

                Matcher nsMatcher = namespacePattern.matcher(rootElement);
                while (nsMatcher.find()) {
                    logger.debug(nsMatcher.group(0));

                    if (nsMatcher.group(1).equals(rootNs)) {
                        return new FetcherResponse(
                                new SequenceInputStream(new ByteArrayInputStream(fileContent), fetcherResponse.getInputStream()),
                                nsMatcher.group(2)
                        );
                    }
                }
            }

            throw new LookupException("Unable to detect namespace.");
        } catch (IOException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
