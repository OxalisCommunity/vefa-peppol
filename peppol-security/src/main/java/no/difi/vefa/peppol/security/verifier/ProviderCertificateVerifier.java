package no.difi.vefa.peppol.security.verifier;

import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.vefa.peppol.common.api.Verifier;
import no.difi.vefa.peppol.common.lang.PeppolException;
import no.difi.vefa.peppol.security.api.PeppolSecurityException;
import no.difi.vefa.peppol.security.context.PeppolContext;
import no.difi.vefa.peppol.security.model.EndpointCertificate;

public class ProviderCertificateVerifier implements Verifier<EndpointCertificate> {

    private Validator validator;

    public ProviderCertificateVerifier(PeppolContext peppolContext) {
        validator = peppolContext.endpointValidator();
    }

    @Override
    public void verify(EndpointCertificate o) throws PeppolException {
        try {
            validator.validate(o.getCertificate());
        } catch (CertificateValidationException e) {
            throw new PeppolSecurityException(e.getMessage(), e);
        }
    }
}
