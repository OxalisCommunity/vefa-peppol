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

package no.difi.vefa.peppol.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ModelUtils {

    public static String urlencode(String format, String... args) {
        try {
            return URLEncoder.encode(String.format(format, args), "UTF-8");
        } catch (UnsupportedEncodingException | NullPointerException e) {
            throw new IllegalStateException("UTF-8 not supported.");
        }
    }

    public static String urldecode(String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException | NullPointerException e) {
            throw new IllegalStateException("UTF-8 not supported.");
        }
    }
}
