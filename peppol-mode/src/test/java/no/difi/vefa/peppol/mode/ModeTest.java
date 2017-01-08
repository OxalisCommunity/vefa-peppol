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

        Mode mode = Mode.of(config, "Some");
        Assert.assertNotNull(mode);
        Assert.assertEquals(mode.getConfig(), config);
    }
}
