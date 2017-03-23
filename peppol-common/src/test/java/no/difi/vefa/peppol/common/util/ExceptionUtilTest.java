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
import no.difi.vefa.peppol.common.lang.PeppolException;
import no.difi.vefa.peppol.common.lang.PeppolRuntimeException;
import org.testng.annotations.Test;

import java.io.IOException;

public class ExceptionUtilTest {

    @Test
    public void simpleConstructor() {
        new ExceptionUtil();
    }

    @Test
    public void simpleNoException() throws Exception {
        ExceptionUtil.perform(Exception.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                // No action.
            }
        });

        ExceptionUtil.perform(Exception.class, new PerformResult<Object>() {
            @Override
            public Object action() throws Exception {
                return null;
            }
        });

        ExceptionUtil.perform(Exception.class, "Not to be thrown.", new PerformResult<Object>() {
            @Override
            public Object action() throws Exception {
                return null;
            }
        });
    }

    @Test(expectedExceptions = IOException.class, expectedExceptionsMessageRegExp = "1337")
    public void simpleThrowIOException() throws Exception {
        ExceptionUtil.perform(IOException.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                throw new Exception("1337");
            }
        });
    }

    @Test(expectedExceptions = PeppolException.class, expectedExceptionsMessageRegExp = "42")
    public void simpleThrowPeppolException() throws Exception {
        ExceptionUtil.perform(PeppolException.class, new PerformResult<Object>() {
            @Override
            public Object action() throws Exception {
                throw new Exception("42");
            }
        });
    }

    @Test(expectedExceptions = PeppolRuntimeException.class, expectedExceptionsMessageRegExp = "1000")
    public void simpleThrowPeppolRuntimeException() throws Exception {
        ExceptionUtil.perform(PeppolRuntimeException.class, "1000", new PerformResult<Object>() {
            @Override
            public Object action() throws Exception {
                throw new Exception("2000");
            }
        });
    }

    @Test(expectedExceptions = PeppolRuntimeException.class)
    public void simpleTooSimpleExtension() throws Exception {
        ExceptionUtil.perform(SimpleExtension.class, new PerformAction() {
            @Override
            public void action() throws Exception {
                throw new Exception("Test");
            }
        });
    }

    public class SimpleExtension extends Exception {

    }
}
