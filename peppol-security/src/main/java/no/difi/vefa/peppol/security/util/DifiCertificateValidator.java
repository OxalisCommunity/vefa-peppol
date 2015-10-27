package no.difi.vefa.peppol.security.util;

import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.vefa.peppol.security.api.*;
import no.difi.vefa.peppol.security.api.PeppolSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;

public class DifiCertificateValidator implements CertificateValidator {

    private static Logger logger = LoggerFactory.getLogger(DifiCertificateValidator.class);

    public Validator validator;

    public DifiCertificateValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(X509Certificate certificate) throws PeppolSecurityException {
        try {
            logger.debug("Validating '{}'.", certificate.getSubjectX500Principal().getName());

            validator.validate(certificate);
        } catch (CertificateValidationException e) {
            throw new PeppolSecurityException(e.getMessage(), e);
        }
    }
}
