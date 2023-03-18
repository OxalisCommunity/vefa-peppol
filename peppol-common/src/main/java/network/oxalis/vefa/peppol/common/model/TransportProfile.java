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

package network.oxalis.vefa.peppol.common.model;

import java.io.Serializable;

public class TransportProfile extends AbstractSimpleIdentifier implements Serializable {

    private static final long serialVersionUID = -8215053834194901976L;

    /**
     * @deprecated  START (busdox-transport-start) is deprecated and replaced by {@link #PEPPOL_AS4_2_0}.
     * START (busdox-transport-start) will be completely removed by 2023-08-24
     */
    @Deprecated
    public static final TransportProfile PEPPOL_START = of("busdox-transport-start");

    /**
     * @deprecated  START (busdox-transport-start) is deprecated and replaced by {@link #PEPPOL_AS4_2_0}.
     * START (busdox-transport-start) will be completely removed by 2023-08-24
     */
    @Deprecated
    public static final TransportProfile START = PEPPOL_START;

    /**
     * @deprecated  AS2v1 (busdox-transport-as2-ver1p0) deprecated in release V7.0 of "codelists" on 2020-04-30 and replaced by {@link #PEPPOL_AS4_2_0}.
     * AS2v1 (busdox-transport-as2-ver1p0) will be completely removed by 2023-08-24
     */
    @Deprecated
    public static final TransportProfile PEPPOL_AS2_1_0 = of("busdox-transport-as2-ver1p0");

    /**
     * @deprecated  AS2v1 (busdox-transport-as2-ver1p0) deprecated in release V7.0 of "codelists" on 2020-04-30 and replaced by {@link #PEPPOL_AS4_2_0}.
     * AS2v1 (busdox-transport-as2-ver1p0) will be completely removed by 2023-08-24
     */
    @Deprecated
    public static final TransportProfile AS2_1_0 = PEPPOL_AS2_1_0;

    /**
     * @deprecated  AS2v2 (busdox-transport-as2-ver2p0) deprecated in release V8.4 of "codelists" on 2023-02-24 and replaced by {@link #PEPPOL_AS4_2_0}.
     * AS2v1 (busdox-transport-as2-ver1p0) will be completely removed by 2023-08-24
     */
    @Deprecated
    public static final TransportProfile PEPPOL_AS2_2_0 = of("busdox-transport-as2-ver2p0");

    public static final TransportProfile PEPPOL_AS4_2_0 = of("peppol-transport-as4-v2_0");

    public static final TransportProfile ESENS_AS4 = of("bdxr-transport-ebms3-as4-v1p0");

    public static final TransportProfile AS4 = ESENS_AS4;

    public static TransportProfile of(String value) {
        return new TransportProfile(value);
    }

    private TransportProfile(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "TransportProfile{" + value + '}';
    }
}
