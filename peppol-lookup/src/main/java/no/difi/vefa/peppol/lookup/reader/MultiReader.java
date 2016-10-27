/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ServiceMetadata;
import no.difi.vefa.peppol.lookup.util.XmlUtils;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.List;

public class MultiReader implements MetadataReader {

    private BusdoxReader busdoxReader = new BusdoxReader();

    private BdxrReader bdxrReader = new BdxrReader();

    @Override
    public List<DocumentTypeIdentifier> parseDocumentIdentifiers(FetcherResponse fetcherResponse)
            throws LookupException {
        FetcherResponse response = fetcherResponse;

        if (response.getNamespace() == null)
            response = detect(response);

        if (BusdoxReader.NAMESPACE.equalsIgnoreCase(response.getNamespace()))
            return busdoxReader.parseDocumentIdentifiers(response);
        else if (BdxrReader.NAMESPACE_201407.equalsIgnoreCase(response.getNamespace())
                || BdxrReader.NAMESPACE_201605.equalsIgnoreCase(response.getNamespace()))
            return bdxrReader.parseDocumentIdentifiers(response);

        throw new LookupException(String.format("Unknown namespace: %s", response.getNamespace()));
    }

    @Override
    public ServiceMetadata parseServiceMetadata(FetcherResponse fetcherResponse)
            throws LookupException, PeppolSecurityException {
        FetcherResponse response = fetcherResponse;

        if (response.getNamespace() == null)
            response = detect(response);

        if (BusdoxReader.NAMESPACE.equalsIgnoreCase(response.getNamespace()))
            return busdoxReader.parseServiceMetadata(response);
        else if (BdxrReader.NAMESPACE_201407.equalsIgnoreCase(response.getNamespace())
                || BdxrReader.NAMESPACE_201605.equalsIgnoreCase(response.getNamespace()))
            return bdxrReader.parseServiceMetadata(response);

        throw new LookupException(String.format("Unknown namespace: %s", response.getNamespace()));
    }

    public FetcherResponse detect(FetcherResponse fetcherResponse) throws LookupException {
        try {
            byte[] fileContent = new byte[1024];
            int size = fetcherResponse.getInputStream().read(fileContent);
            fileContent = Arrays.copyOfRange(fileContent, 0, size);

            String namespace = XmlUtils.extractRootNamespace(new String(fileContent));
            if (namespace != null) {
                return new FetcherResponse(
                        new SequenceInputStream(
                                new ByteArrayInputStream(fileContent),
                                fetcherResponse.getInputStream()),
                        namespace
                );
            }

            throw new LookupException("Unable to detect namespace.");
        } catch (IOException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
