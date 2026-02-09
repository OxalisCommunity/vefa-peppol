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

import javax.security.auth.x500.X500Principal;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public final class X500DnComparator {

    private X500DnComparator() {
    }

    public static boolean equalsSemantically(X500Principal certificateDn,
                                             String smpRedirectDistinguishedName) {

        if (certificateDn == null || smpRedirectDistinguishedName == null) {
            return false;
        }

        try {
            X500Principal redirectDn = new X500Principal(smpRedirectDistinguishedName);

            Set<String> certRdns = normalize(certificateDn);
            Set<String> redirectRdns = normalize(redirectDn);

            return certRdns.equals(redirectRdns);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static Set<String> normalize(X500Principal principal) {
        String rfc2253 = principal.getName(X500Principal.RFC2253);

        Set<String> rdns = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        Arrays.stream(rfc2253.split(","))
                .map(String::trim)
                .forEach(rdns::add);

        return rdns;
    }
}
