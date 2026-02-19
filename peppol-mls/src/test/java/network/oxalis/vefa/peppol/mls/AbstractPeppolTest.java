/*
 * Copyright 2015-2026 Direktoratet for forvaltning og IKT
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

package network.oxalis.vefa.peppol.mls;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.fail;

public abstract class AbstractPeppolTest {
    protected static final String SENDER_ID = "POP000723";
    protected static final String RECEIVER_ID = "POP000010";
    protected static final String SCHEME_ID = "0242";

    protected String loadXml(String path) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected void assertDoesNotThrow(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            fail("Expected no exception, but got: " + t.getMessage());
        }
    }

    protected PeppolMLSBuilder defaultBuilder() {
        return new PeppolMLSBuilder(
                SENDER_ID,
                SCHEME_ID,
                RECEIVER_ID,
                SCHEME_ID);
    }

    protected PeppolMLSLineResponseBuilder lineResponse(String id, String description, MLSStatusReasonCode reasonCode) {
        return new PeppolMLSLineResponseBuilder(id)
                .addResponse(description, reasonCode);
    }
}
