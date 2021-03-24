package network.oxalis.vefa.peppol.common.model;

import java.io.Serializable;

public class Redirect implements Serializable {

    private static final long serialVersionUID = -2614225903432448149L;

    private final String certificateUID;
    private final String href;

    public static Redirect of(String certificateUID, String href) {
        return new Redirect(certificateUID, href);
    }

    private Redirect(String certificateUID, String href) {
        this.certificateUID = certificateUID;
        this.href = href;
    }

    public String getCertificateUID() {
        return certificateUID;
    }

    public String getHref() {
        return href;
    }
}
