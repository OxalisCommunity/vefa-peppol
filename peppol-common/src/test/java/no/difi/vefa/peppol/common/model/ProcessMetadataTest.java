/*
 * Copyright 2016-2017 Direktoratet for forvaltning og IKT
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

package no.difi.vefa.peppol.common.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

public class ProcessMetadataTest {

    @Test
    public void simple() {
        ProcessIdentifier processIdentifier1 = ProcessIdentifier.of("Some:Process");
        ProcessIdentifier processIdentifier2 = ProcessIdentifier.of("Other:Process");
        Endpoint endpoint1 = Endpoint.of(TransportProfile.AS2_1_0, URI.create("http://localhost/as2"), null);
        Endpoint endpoint2 = Endpoint.of(TransportProfile.START, URI.create("http://localhost/start"), null);

        ProcessMetadata processMetadata = ProcessMetadata.of(processIdentifier1, endpoint1);

        Assert.assertNotNull(processMetadata.hashCode());
        Assert.assertNotNull(processMetadata.toString());

        Assert.assertFalse(processMetadata.equals(null));
        Assert.assertTrue(processMetadata.equals(processMetadata));
        Assert.assertFalse(processMetadata.equals(new Object()));

        Assert.assertTrue(processMetadata.equals(ProcessMetadata.of(processIdentifier1, endpoint1)));
        Assert.assertFalse(processMetadata.equals(ProcessMetadata.of(processIdentifier2, endpoint1)));
        Assert.assertFalse(processMetadata.equals(ProcessMetadata.of(processIdentifier1, endpoint2)));
        Assert.assertFalse(processMetadata.equals(ProcessMetadata.of(processIdentifier2, endpoint2)));

        Assert.assertEquals(processMetadata.getEndpoints().size(), 1);
    }
}
