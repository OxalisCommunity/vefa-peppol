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

import no.difi.vefa.peppol.common.api.PerformAction;
import no.difi.vefa.peppol.common.api.PerformResult;
import no.difi.vefa.peppol.common.lang.PeppolRuntimeException;

public class ExceptionUtil {

    public static <E extends Exception> void perform(Class<E> cls, PerformAction action) throws E {
        try {
            action.action();
        } catch (Exception e) {
            throw prepareException(cls, e.getMessage(), e);
        }
    }

    public static <T, E extends Exception> T perform(Class<E> cls, PerformResult<T> action) throws E {
        try {
            return action.action();
        } catch (Exception e) {
            throw prepareException(cls, e.getMessage(), e);
        }
    }

    public static <T, E extends Exception> T perform(Class<E> cls, String message, PerformResult<T> action) throws E {
        try {
            return action.action();
        } catch (Exception e) {
            throw prepareException(cls, message, e);
        }
    }

    private static <E extends Exception> E prepareException(Class<E> cls, String message, Throwable throwable)
            throws E {
        try {
            return cls.getConstructor(String.class, Throwable.class).newInstance(message, throwable);
        } catch (Exception e) {
            throw new PeppolRuntimeException(String.format("Unable to initiate exception '%s'.", cls), e);
        }
    }
}
