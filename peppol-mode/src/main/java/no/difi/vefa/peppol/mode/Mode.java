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

import java.lang.reflect.InvocationTargetException;

public class Mode {

    public static final String PRODUCTION = "PRODUCTION";

    public static final String TEST = "TEST";

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
