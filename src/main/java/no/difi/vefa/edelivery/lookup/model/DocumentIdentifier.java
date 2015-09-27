package no.difi.vefa.edelivery.lookup.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * DocumentIdentifier is a combination of XML type and customizationId.
 *
 * Pattern: [xml namespace]::[xml root element]##[customizationId]::[xml version]
 */
public class DocumentIdentifier implements Serializable {

    private static final long serialVersionUID = -3748163459655880167L;

    private String scheme;
    private String customizationId;
    private String xmlNamespace;
    private String xmlRootElement;
    private String xmlVersion;

    public DocumentIdentifier(String documentIdentifier) {
        this(documentIdentifier, "busdox-docid-qns");
    }

    public DocumentIdentifier(String documentIdentifier, String scheme) {
        String[] parts = documentIdentifier.split("::|##");

        xmlNamespace = parts[0];
        xmlRootElement = parts[1];
        customizationId = parts[2];
        xmlVersion = parts[3];

        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }

    public String getIdentifier() {
        return String.format("%s::%s##%s::%s", xmlNamespace, xmlRootElement, customizationId, xmlVersion);
    }

    public String getCustomizationId() {
        return customizationId;
    }

    public String getXmlNamespace() {
        return xmlNamespace;
    }

    public String getXmlRootElement() {
        return xmlRootElement;
    }

    public String getXmlVersion() {
        return xmlVersion;
    }

    @Override
    public String toString() {
        return String.format("%s::%s", scheme, getIdentifier());
    }

    public String urlencoded() {
        try {
            return URLEncoder.encode(String.format("%s::%s", scheme, getIdentifier()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported.");
        }
    }

}
