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

import java.lang.reflect.InvocationTargetException;

public class Mode {

    public static final String PRODUCTION = "PRODUCTION";

    public static final String TEST = "TEST";

    private Config config;

    private String identifier;

    public static Mode of(String identifier) {
        return of(ConfigFactory.load(), identifier);
    }

    public static Mode of(Config config, String identifier) {
        // Loading configuration based on identifier.
        if (identifier != null)
            if (config.hasPath(String.format("mode.%s", identifier)))
                config = config.withFallback(config.getConfig(String.format("mode.%s", identifier)));

        // Load inherited configuration.
        if (config.hasPath("inherit"))
            config = config.withFallback(config.getConfig(String.format("mode.%s", config.getString("inherit"))));

        // Load default configuration.
        if (config.hasPath("mode.default"))
            config = config.withFallback(config.getConfig("mode.default"));

        return new Mode(config, identifier);
    }

    private Mode(Config config, String identifier) {
        this.config = config;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public Config getConfig() {
        return config;
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T> T initiate(String key, Class<T> type) throws PeppolLoadingException {
        try {
            return (T) initiate(Class.forName(getString(key)));
        } catch (ClassNotFoundException e) {
            throw new PeppolLoadingException(String.format("Unable to initiate '%s'", getString(key)), e);
        }
    }

    public <T> T initiate(Class<T> cls) throws PeppolLoadingException {
        try {
            try {
                return cls.getConstructor(Mode.class).newInstance(this);
            } catch (NoSuchMethodException e) {
                return cls.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new PeppolLoadingException(String.format("Unable to initiate '%s'", cls), e);
        }
    }
}
