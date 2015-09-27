package no.difi.vefa.edelivery.lookup.model;

import java.io.InputStream;

public class FetcherResponse {

    private InputStream inputStream;
    private String namespace;

    public FetcherResponse(InputStream inputStream, String namespace) {
        this.inputStream = inputStream;
        this.namespace = namespace;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getNamespace() {
        return namespace;
    }
}
