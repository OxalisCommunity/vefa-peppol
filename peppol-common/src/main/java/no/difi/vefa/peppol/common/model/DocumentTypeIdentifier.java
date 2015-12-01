package no.difi.vefa.peppol.common.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * DocumentTypeIdentifier is a combination of XML type and customizationId.
 *
 * Pattern: [xml namespace]::[xml root element]##[customizationId]::[xml version]
 */
public class DocumentTypeIdentifier implements Serializable {

    private static final long serialVersionUID = -3748163459655880167L;

    public static final Scheme DEFAULT_SCHEME = new Scheme("busdox-docid-qns");

    private Scheme scheme;
    private String customizationId;
    private String xmlNamespace;
    private String xmlRootElement;
    private String xmlVersion;
    private URI uri;

    public DocumentTypeIdentifier(String documentIdentifier) {
        this(documentIdentifier, DEFAULT_SCHEME, null);
    }

    public DocumentTypeIdentifier(String documentIdentifier, Scheme scheme) {
        this(documentIdentifier, scheme, null);
    }

    public DocumentTypeIdentifier(String documentIdentifier, Scheme scheme, URI uri) {
        String[] parts = documentIdentifier.split("::|##");

        xmlNamespace = parts[0];
        xmlRootElement = parts[1];
        customizationId = parts[2];
        xmlVersion = parts[3];

        this.scheme = scheme;
        this.uri = uri;
    }

    public Scheme getScheme() {
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

    public URI getUri() {
        return uri;
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
