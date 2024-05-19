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

package network.oxalis.vefa.peppol.mode;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import network.oxalis.vefa.peppol.common.lang.PeppolLoadingException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Mode {

    public static final String PRODUCTION = "PRODUCTION";

    public static final String TEST = "TEST";

    public static final String FRTEST = "FRTEST";

    private Config config;

    private String identifier;

    public static Mode of(String identifier) {
        return of(ConfigFactory.empty(), identifier);
    }

    public static Mode of(Config config, String identifier) {
        Config referenceConfig = ConfigFactory.defaultReference();

        Config result = ConfigFactory.systemProperties()
                .withFallback(config)
                .withFallback(referenceConfig);

        // Loading configuration based on identifier.
        if (identifier != null)
            if (referenceConfig.hasPath(String.format("mode.%s", identifier)))
                result = result.withFallback(
                        referenceConfig.getConfig(String.format("mode.%s", identifier)));

        // Load inherited configuration.
        if (result.hasPath("inherit"))
            result = result.withFallback(
                    referenceConfig.getConfig(String.format("mode.%s", result.getString("inherit"))));

        // Load default configuration.
        if (referenceConfig.hasPath("mode.default"))
            result = result.withFallback(referenceConfig.getConfig("mode.default"));

        return new Mode(result, identifier);
    }

    private Mode(Config config, String identifier) {
        this.config = config;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean hasString(String key) {
        return config.hasPath(key);
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public Config getConfig() {
        return config;
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T> T initiate(String key, Class<T> type, Map<String, Object> objectStorage) throws PeppolLoadingException {
        try {
            return (T) initiate(Class.forName(getString(key)), objectStorage);
        } catch (ClassNotFoundException e) {
            throw new PeppolLoadingException(String.format("Unable to initiate '%s'", getString(key)), e);
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T> T initiate(String key, Class<T> type) throws PeppolLoadingException {
        return initiate(key, type, null);
    }

    public <T> T initiate(Class<T> cls) throws PeppolLoadingException {
        return initiate(cls, null);
    }

    public <T> T initiate(Class<T> cls, Map<String, Object> objectStorage) throws PeppolLoadingException {
        try {
            try {
                return cls.getConstructor(Mode.class, Map.class).newInstance(this, objectStorage);
            } catch (NoSuchMethodException e) {
                // No action
            }

            try {
                return cls.getConstructor(Mode.class).newInstance(this);
            } catch (NoSuchMethodException e) {
                // No action
            }

            return cls.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new PeppolLoadingException(String.format("Unable to initiate '%s'", cls), e);
        }
    }
}
