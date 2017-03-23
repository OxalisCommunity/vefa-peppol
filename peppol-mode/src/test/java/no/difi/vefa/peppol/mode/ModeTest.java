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

package no.difi.vefa.peppol.mode;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import no.difi.vefa.peppol.common.lang.PeppolLoadingException;
import no.difi.vefa.peppol.mode.classes.ProductionObject;
import no.difi.vefa.peppol.mode.classes.SomeObject;
import no.difi.vefa.peppol.mode.classes.TestObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ModeTest {

    @Test
    public void simpleTest() throws Exception {
        Mode mode = Mode.of(Mode.TEST);
        Assert.assertEquals(mode.getString("value"), "Hello");
        Assert.assertTrue(mode.initiate("class", SomeObject.class) instanceof TestObject);
        Assert.assertNotNull(mode.getConfig());
    }

    @Test
    public void simpleProduction() throws Exception  {
        Mode mode = Mode.of(Mode.PRODUCTION);
        Assert.assertEquals(mode.getString("value"), "World");
        Assert.assertTrue(mode.initiate("class", SomeObject.class) instanceof ProductionObject);
        Assert.assertNotNull(mode.getConfig());
    }

    @Test
    public void simpleInherit() throws Exception {
        Mode mode = Mode.of("ERROR");
        Assert.assertEquals(mode.getString("value"), "World");
    }

    @Test(expectedExceptions = PeppolLoadingException.class)
    public void simpleClassNotFound() throws Exception {
        Mode.of("ERROR").initiate("class", SomeObject.class);
    }

    @Test(expectedExceptions = PeppolLoadingException.class)
    public void simpleInvalid() throws Exception {
        Mode.of("INVALID").initiate("class", SomeObject.class);
    }

    @Test
    public void simpleNonExisting() throws Exception {
        Mode mode = Mode.of("Unknown");
        Assert.assertNotNull(mode);
        Assert.assertEquals(mode.getIdentifier(), "Unknown");
    }

    @Test
    public void simpleEmpty() throws Exception {
        Config config = ConfigFactory.empty();

        Mode mode = Mode.of(config, null);
        Assert.assertNotNull(mode);
        Assert.assertNotEquals(mode.getConfig(), config);
    }

    @Test(expectedExceptions = PeppolLoadingException.class)
    public void simpleLoadingException() throws Exception {
        Mode.of("EXCEPTION").initiate("class", SomeObject.class);
    }
}
