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

    private static <E extends Exception> E prepareException(Class<E> cls, String message, Throwable throwable) throws E {
        try {
            return cls.getConstructor(String.class, Throwable.class).newInstance(message, throwable);
        } catch (Exception e) {
            throw new PeppolRuntimeException(String.format("Unable to initiate exception '%s'.", cls), e);
        }
    }
}
