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

package network.oxalis.vefa.peppol.lookup.util;

import org.testng.annotations.Test;

import javax.security.auth.x500.X500Principal;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class X500DnComparatorTest {

    @Test
    public void semanticDnComparisonSucceedsForSameDnInDifferentOrder() {
        X500Principal certDn = new X500Principal("C=SE, O=Tickstar, OU=PEPPOL PRODUCTION SMP, CN=PSE000018");
        String smpDn = "CN=PSE000018,OU=PEPPOL PRODUCTION SMP,O=Tickstar,C=SE";

        assertTrue(
                X500DnComparator.equalsSemantically(certDn, smpDn),
                "Semantic DN comparison must ignore RDN ordering"
        );
    }

    @Test
    public void semanticDnComparisonHandlesSpacingDifferences() {
        X500Principal certDn = new X500Principal("CN=PSE000018,OU=PEPPOL PRODUCTION SMP,O=Tickstar,C=SE");
        String smpDn = "CN=PSE000018, OU=PEPPOL PRODUCTION SMP,O=Tickstar, C=SE";

        assertTrue(
                X500DnComparator.equalsSemantically(certDn, smpDn),
                "Whitespace differences must not affect DN comparison"
        );
    }

    @Test
    public void semanticDnComparisonFailsForDifferentDn() {
        X500Principal certDn = new X500Principal("CN=POP000010,OU=PEPPOL PRODUCTION SMP,O=Basware,C=FI");
        String smpDn = "CN=PFI000010,OU=PEPPOL PRODUCTION SMP,O=Basware,C=FI";

        assertFalse(
                X500DnComparator.equalsSemantically(certDn, smpDn),
                "Different Subject DNs must not match"
        );
    }

    @Test
    public void semanticDnComparisonHandlesNullValues() {
        X500Principal certDn = new X500Principal("CN=PSE000018,OU=PEPPOL PRODUCTION SMP,O=Tickstar,C=SE");

        assertFalse(
                X500DnComparator.equalsSemantically(null, "CN=PSE000018,OU=PEPPOL PRODUCTION SMP,O=Tickstar,C=SE"),
                "Null certificate DN must fail comparison"
        );

        assertFalse(
                X500DnComparator.equalsSemantically(certDn, null),
                "Null SMP DN must fail comparison"
        );

        assertFalse(
                X500DnComparator.equalsSemantically(null, null),
                "Both null values must fail comparison"
        );
    }

    @Test
    public void malformedDnMustNotMatch() {
        X500Principal certDn = new X500Principal("CN=PSE000018,OU=PEPPOL PRODUCTION SMP,O=Tickstar,C=SE");
        String malformedDn = "CN==PSE000018,O=Tickstar,C=SE";

        assertFalse(
                X500DnComparator.equalsSemantically(certDn, malformedDn),
                "Malformed DN must not be treated as equal"
        );
    }

    @Test
    public void caseInsensitiveAttributeTypes() {
        X500Principal certDn = new X500Principal("CN=PSE000018,OU=PEPPOL PRODUCTION SMP,O=Tickstar,C=SE");
        String smpDn = "cn=pse000018,ou=peppol production smp,o=tickstar,c=se";

        assertTrue(
                X500DnComparator.equalsSemantically(certDn, smpDn),
                "DN comparison must be case-insensitive for attribute types and values"
        );
    }

    @Test
    public void realWorldTickstarExample() {
        X500Principal certDn = new X500Principal("CN=PSE000018,OU=PEPPOL PRODUCTION SMP,O=Tickstar,C=SE");
        String smpDn = "C=SE, O=Tickstar, OU=PEPPOL PRODUCTION SMP, CN=PSE000018";

        assertTrue(
                X500DnComparator.equalsSemantically(certDn, smpDn),
                "Real-world Tickstar SMP redirect DN must match certificate subject"
        );
    }

    @Test
    public void norwegianELMATestSmpExample() {
        X500Principal certDn = new X500Principal("CN=PNO000179,OU=PEPPOL TEST SMP,O=The Norwegian Digitalisation Agency,C=NO");
        String smpDn = "CN=PNO000179,OU=PEPPOL TEST SMP,O=The Norwegian Digitalisation Agency,C=NO";

        assertTrue(
                X500DnComparator.equalsSemantically(certDn, smpDn),
                "Norwegian test SMP DN must match semantically"
        );
    }
}
