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
import no.difi.vefa.peppol.common.lang.PeppolException;
import no.difi.vefa.peppol.common.lang.PeppolRuntimeException;
import org.testng.annotations.Test;

import java.io.IOException;

public class ExceptionUtilTest {

    @Test
    public void simpleConstructor() {
        new ExceptionUtil();
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
}
