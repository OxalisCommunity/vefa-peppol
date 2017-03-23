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

package no.difi.vefa.peppol.publisher;

import com.google.common.collect.Lists;
import no.difi.vefa.peppol.publisher.annotation.Syntax;
import no.difi.vefa.peppol.publisher.api.PublisherSyntax;

import java.util.*;

/**
 * @author erlend
 */
public class PublisherSyntaxProvider {

    private Map<String, PublisherSyntax> syntaxMap = new HashMap<>();

    private String defaultSyntax;

    public PublisherSyntaxProvider(String defaultSyntax) {
        this(defaultSyntax, Lists.newArrayList(ServiceLoader.load(PublisherSyntax.class).iterator()));

        for (PublisherSyntax syntax : ServiceLoader.load(PublisherSyntax.class))
            for (String syntaxKey : syntax.getClass().getAnnotation(Syntax.class).value())
                syntaxMap.put(syntaxKey, syntax);
    }

    public PublisherSyntaxProvider(String defaultSyntax, List<PublisherSyntax> publisherSyntaxes) {
        this.defaultSyntax = defaultSyntax;

        for (PublisherSyntax syntax : publisherSyntaxes)
            for (String syntaxKey : syntax.getClass().getAnnotation(Syntax.class).value())
                syntaxMap.put(syntaxKey, syntax);
    }

    protected PublisherSyntax getSyntax(String syntax) {
        if (syntax != null && syntaxMap.containsKey(syntax))
            return syntaxMap.get(syntax);

        return syntaxMap.get(defaultSyntax);
    }
}
