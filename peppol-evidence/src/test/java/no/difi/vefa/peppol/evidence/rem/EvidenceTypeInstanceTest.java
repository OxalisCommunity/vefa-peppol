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

package no.difi.vefa.peppol.evidence.rem;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EvidenceTypeInstanceTest {

    @Test
    public void simpleGetLocalName() {
        Assert.assertNotNull(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT);
        Assert.assertNotNull(EvidenceTypeInstance.RELAY_REM_MD_ACCEPTANCE_REJECTION);
    }

    @Test
    public void simpleFindByLocalName() {
        Assert.assertNotNull(EvidenceTypeInstance.findByLocalName("DeliveryNonDeliveryToRecipient"));
        Assert.assertNull(EvidenceTypeInstance.findByLocalName("test"));
    }

    @Test
    public void simpleValueOf() {
        Assert.assertEquals(EvidenceTypeInstance.valueOf(EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT.toString()), EvidenceTypeInstance.DELIVERY_NON_DELIVERY_TO_RECIPIENT);
    }
}
