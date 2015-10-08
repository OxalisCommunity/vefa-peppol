package no.difi.vefa.peppol.lookup.api;

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
