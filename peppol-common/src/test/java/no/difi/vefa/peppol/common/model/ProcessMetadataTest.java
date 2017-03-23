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

        Assert.assertEquals(processMetadata.getTransportProfiles().size(), 1);

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
