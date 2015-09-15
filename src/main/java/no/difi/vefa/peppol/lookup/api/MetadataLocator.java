package no.difi.vefa.peppol.lookup.api;

import no.difi.vefa.peppol.lookup.model.ParticipantIdentifier;

import java.net.URI;

public interface MetadataLocator {

    URI lookup(String identifier) throws LookupException;

    URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException;
}
