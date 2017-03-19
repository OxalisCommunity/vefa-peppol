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
