package no.difi.vefa.edelivery.lookup;

import no.difi.vefa.edelivery.lookup.api.*;
import no.difi.vefa.edelivery.lookup.api.SecurityException;
import no.difi.vefa.edelivery.lookup.model.DocumentIdentifier;
import no.difi.vefa.edelivery.lookup.model.ParticipantIdentifier;
import no.difi.vefa.edelivery.lookup.model.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

public class LookupClient {

    private static Logger logger = LoggerFactory.getLogger(LookupClient.class);

    private MetadataLocator metadataLocator;
    private MetadataProvider metadataProvider;

    private MetadataFetcher metadataFetcher;
    private MetadataReader metadataReader;

    LookupClient(MetadataLocator metadataLocator, MetadataProvider metadataProvider, MetadataFetcher metadataFetcher, MetadataReader metadataReader) {
        this.metadataLocator = metadataLocator;
        this.metadataProvider = metadataProvider;
        this.metadataFetcher = metadataFetcher;
        this.metadataReader = metadataReader;
    }

    public List<DocumentIdentifier> getDocumentIdentifiers(ParticipantIdentifier participantIdentifier) throws LookupException {
        URI location = metadataLocator.lookup(participantIdentifier);
        URI provider = metadataProvider.resolveDocumentIdentifiers(location, participantIdentifier);

        logger.debug("{}", provider);

        return metadataReader.parseDocumentIdentifiers(metadataFetcher.fetch(provider));
    }

    public ServiceMetadata getServiceMetadata(ParticipantIdentifier participantIdentifier, DocumentIdentifier documentIdentifier) throws LookupException, SecurityException {
        URI location = metadataLocator.lookup(participantIdentifier);
        URI provider = metadataProvider.resolveServiceMetadata(location, participantIdentifier, documentIdentifier);

        logger.debug("{}", provider);

        ServiceMetadata serviceMetadata = metadataReader.parseServiceMetadata(metadataFetcher.fetch(provider));

        // TODO Validate provider certificate

        return serviceMetadata;
    }

}
