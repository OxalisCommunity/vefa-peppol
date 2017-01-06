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

import java.util.List;

public class Mode {

    public static final String PRODUCTION = "PRODUCTION";
    public static final String TEST = "TEST";

    private Config config;

    public static Mode of(String mode) {
        Config config = ConfigFactory.load();
        return of(config.getConfig(String.format("vefa.mode.%s", mode))
                .withFallback(config.getConfig("vefa.mode.default")));
    }

    public static Mode of(Config config) {
        return new Mode(config);
    }

    private Mode(Config config) {
        this.config = config;
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public List<String> getStringList(String key) {
        return config.getStringList(key);
    }

    public Config getConfig() {
        return config;
    }
}
