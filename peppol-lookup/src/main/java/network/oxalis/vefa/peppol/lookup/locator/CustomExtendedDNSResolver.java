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

package network.oxalis.vefa.peppol.lookup.locator;

import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.SimpleResolver;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CustomExtendedDNSResolver {

    private CustomExtendedDNSResolver() {
    }

    public static void setCustomizeResolverTimeout(Resolver resolver, long timeout) {
        // Set query timeout
        resolver.setTimeout(Duration.ofSeconds(timeout));
    }

    public static void setCustomizeResolverTimeoutAndMaxRetries(ExtendedResolver extendedResolver, long timeout, int maxRetries) {
        setCustomizeResolverTimeout(extendedResolver, timeout);
        // Set the default retries
        extendedResolver.setRetries(maxRetries);
    }

    public static void iterateEachDefaultResolverAndConfigure(Consumer<? super SimpleResolver> consumer, long timeout) {
        for (final InetSocketAddress inetSocketAddress : ResolverConfig.getCurrentConfig().servers())
            if (inetSocketAddress != null) {
                SimpleResolver simpleResolver = new SimpleResolver(inetSocketAddress);
                setCustomizeResolverTimeout(simpleResolver, timeout);
                consumer.accept(simpleResolver);
            }
    }

    public static void iterateEachResolverAndConfigure(Iterable<? extends InetAddress> customDNSServersAddress,
                                                       Consumer<? super SimpleResolver> aConsumer, long timeout) {
        if (customDNSServersAddress != null)
            for (final InetAddress inetAddress : customDNSServersAddress)
                if (inetAddress != null) {
                    // Use the default port
                    final SimpleResolver simpleResolver = new SimpleResolver(inetAddress);
                    setCustomizeResolverTimeout(simpleResolver, timeout);
                    aConsumer.accept(simpleResolver);
                }
    }

    private static boolean isResolverAlreadyAvailable(List<Resolver> resolverList, SimpleResolver simpleResolver) {
        final InetSocketAddress aSearchAddr = simpleResolver.getAddress();

        // Use Stream API to check if any resolver has the same address
        return resolverList.stream()
                .filter(x -> x instanceof SimpleResolver)
                .map(x -> (SimpleResolver) x)
                .anyMatch(x -> x.getAddress().equals(aSearchAddr));
    }

    public static ExtendedResolver createExtendedResolver(final Iterable<? extends InetAddress> customDNSServersAddress, long timeout, int maxRetries) {
        List<Resolver> resolverList = new ArrayList<>();

        iterateEachResolverAndConfigure(customDNSServersAddress, x -> {
            if (!isResolverAlreadyAvailable(resolverList, x))
                resolverList.add(x);
        }, timeout);

        iterateEachDefaultResolverAndConfigure(x -> {
            if (!isResolverAlreadyAvailable(resolverList, x))
                resolverList.add(x);
        }, timeout);

        ExtendedResolver extendedResolver = new ExtendedResolver(resolverList);
        setCustomizeResolverTimeoutAndMaxRetries(extendedResolver, timeout, maxRetries);
        return extendedResolver;
    }
}


