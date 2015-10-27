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
import sun.security.provider.certpath.OCSP;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PeppolContext {

    private static final Map<String, String> apCn = new HashMap<String, String>() {{
        put("test", "PEPPOL ACCESS POINT TEST CA");
        put("production", "PEPPOL ACCESS POINT CA");
    }};
    private static final Map<String, String> smpCn = new HashMap<String, String>() {{
        put("test", "PEPPOL SERVICE METADATA PUBLISHER TEST CA");
        put("production", "PEPPOL SERVICE METADATA PUBLISHER CA");
    }};

    private CrlCache crlCache = new SimpleCrlCache();
    private KeyStoreCertificateBucket keyStore;
    private CertificateBucket rootCertificates;
    private CertificateBucket intermediateApCertificates;
    private CertificateBucket intermediateSmpCertificates;

    private String scope;

    public PeppolContext(String scope) {
        try {
            this.scope = scope;
            keyStore = new KeyStoreCertificateBucket(getKeyStoreInputStream(scope), "peppol");

            rootCertificates = keyStore.toSimple("peppol-root", "difi-root");
            intermediateApCertificates = keyStore.toSimple("peppol-ap", "difi-ap");
            intermediateSmpCertificates = keyStore.toSimple("peppol-smp", "difi-smp");
        } catch (CertificateBucketException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static InputStream getKeyStoreInputStream(String scope) {
        return PeppolContext.class.getResourceAsStream(String.format("/peppol-%s.jks", scope));
    }

    public KeyStoreCertificateBucket getKeyStoreBucket() {
        return keyStore;
    }

    public Validator apValidator() {
        return generateValidator(intermediateApCertificates, apCn);
    }

    public Validator smpValidator() {
        return generateValidator(intermediateSmpCertificates, smpCn);
    }

    private Validator generateValidator(CertificateBucket intermediateCertificates, Map<String, String> cnMap) {
        ValidatorBuilder validatorBuilder = ValidatorBuilder.newInstance();
        validatorBuilder.addRule(new ExpirationRule());
        validatorBuilder.addRule(SigningRule.PublicSignedOnly());

        if (cnMap != null && cnMap.containsKey(scope))
            validatorBuilder.addRule(new PrincipalNameRule("CN", new SimplePrincipalNameProvider(cnMap.get(scope)), PrincipalNameRule.Principal.ISSUER));

        validatorBuilder.addRule(new ChainRule(rootCertificates, intermediateCertificates));
        validatorBuilder.addRule(new CRLRule(crlCache));

        if (!"test".endsWith(scope)) // TODO Remove when OCSP for test certificates respond correct.
            validatorBuilder.addRule(new OCSPRule(intermediateCertificates));

        return validatorBuilder.build();
    }
}
