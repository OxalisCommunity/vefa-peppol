package no.difi.vefa.peppol.security.context;


import no.difi.certvalidator.Validator;
import no.difi.certvalidator.ValidatorBuilder;
import no.difi.certvalidator.api.CertificateBucket;
import no.difi.certvalidator.api.CertificateBucketException;
import no.difi.certvalidator.api.CrlCache;
import no.difi.certvalidator.rule.*;
import no.difi.certvalidator.util.KeyStoreCertificateBucket;
import no.difi.certvalidator.util.SimpleCrlCache;
import no.difi.certvalidator.util.SimplePrincipalNameProvider;

import java.io.InputStream;

public class PeppolProduction {

    private static CrlCache crlCache = new SimpleCrlCache();
    private static KeyStoreCertificateBucket keyStore;
    private static CertificateBucket rootCertificates;
    private static CertificateBucket intermediateApCertificates;
    private static CertificateBucket intermediateSmpCertificates;

    static {
        try {
            keyStore = new KeyStoreCertificateBucket(getKeyStoreInputStream(), "peppol");

            rootCertificates = keyStore.toSimple("peppol-root");
            intermediateApCertificates = keyStore.toSimple("peppol-ap");
            intermediateSmpCertificates = keyStore.toSimple("peppol-smp");
        } catch (CertificateBucketException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static InputStream getKeyStoreInputStream() {
        return PeppolProduction.class.getResourceAsStream("/peppol-production.jks");
    }

    public static KeyStoreCertificateBucket getKeyStoreBucket() {
        return keyStore;
    }

    public static Validator apValidator() {
        return ValidatorBuilder.newInstance()
                .addRule(new ExpirationRule())
                .addRule(new SigningRule())
                .addRule(new PrincipalNameRule("CN", new SimplePrincipalNameProvider("PEPPOL ACCESS POINT CA"), PrincipalNameRule.Principal.ISSUER))
                .addRule(new ChainRule(rootCertificates, intermediateApCertificates))
                .addRule(new CRLRule(crlCache))
                .addRule(new OCSPRule(intermediateApCertificates))
                .build();
    }

    public static Validator smpValidator() {
        return ValidatorBuilder.newInstance()
                .addRule(new ExpirationRule())
                .addRule(new SigningRule())
                .addRule(new PrincipalNameRule("CN", new SimplePrincipalNameProvider("PEPPOL SERVICE METADATA PUBLISHER CA"), PrincipalNameRule.Principal.ISSUER))
                .addRule(new ChainRule(rootCertificates, intermediateSmpCertificates))
                .addRule(new CRLRule(crlCache))
                .addRule(new OCSPRule(intermediateSmpCertificates))
                .build();
    }


}
