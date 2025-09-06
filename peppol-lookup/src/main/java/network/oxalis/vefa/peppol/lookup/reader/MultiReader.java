/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package network.oxalis.vefa.peppol.lookup.reader;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import network.oxalis.vefa.peppol.common.api.PotentiallySigned;
import network.oxalis.vefa.peppol.common.model.ServiceMetadata;
import network.oxalis.vefa.peppol.common.model.ServiceReference;
import network.oxalis.vefa.peppol.lookup.api.FetcherResponse;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.MetadataReader;
import network.oxalis.vefa.peppol.lookup.api.Namespace;
import network.oxalis.vefa.peppol.lookup.util.XmlUtils;
import network.oxalis.vefa.peppol.security.lang.PeppolSecurityException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;

public class MultiReader implements MetadataReader {

    private static final List<MetadataReader> METADATA_READERS = Lists.newArrayList(ServiceLoader.load(MetadataReader.class));

    @Override
    public List<ServiceReference> parseServiceGroup(FetcherResponse fetcherResponse) throws LookupException {
        FetcherResponse response = fetcherResponse;

        if (response.getNamespace() == null)
            response = detect(response);

        for (MetadataReader metadataReader : METADATA_READERS)
            if (metadataReader.getClass().getAnnotation(Namespace.class).value().equalsIgnoreCase(response.getNamespace()))
                return metadataReader.parseServiceGroup(response);

        throw new LookupException(String.format("Unknown namespace: %s", response.getNamespace()));
    }

    @Override
    public PotentiallySigned<ServiceMetadata> parseServiceMetadata(FetcherResponse fetcherResponse)
            throws LookupException, PeppolSecurityException {
        FetcherResponse response = fetcherResponse;

        if (response.getNamespace() == null)
            response = detect(response);

        for (MetadataReader metadataReader : METADATA_READERS)
            if (metadataReader.getClass().getAnnotation(Namespace.class).value().equalsIgnoreCase(response.getNamespace()))
                return metadataReader.parseServiceMetadata(response);

        throw new LookupException(String.format("Unknown namespace: %s", response.getNamespace()));
    }

    public FetcherResponse detect(FetcherResponse fetcherResponse) throws LookupException {
        try {
            byte[] fileContent = ByteStreams.toByteArray(fetcherResponse.getInputStream());

            String namespace = XmlUtils.extractRootNamespace(new String(fileContent));
            if (namespace != null)
                return new FetcherResponse(new ByteArrayInputStream(fileContent), namespace);

            throw new LookupException("Unable to detect namespace.");
        } catch (IOException e) {
            throw new LookupException(e.getMessage(), e);
        }
    }
}
