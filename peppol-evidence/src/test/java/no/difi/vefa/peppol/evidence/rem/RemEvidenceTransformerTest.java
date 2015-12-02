package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.security.api.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * Created by steinar on 08.11.2015.
 */
public class RemEvidenceTransformerTest {

    public void setUp() {

    }

    @Test
    public void buildStreamAndParseRemEvidence() throws Exception {

        // Obtains instance of the service which is the entry point to the Rem package
        RemEvidenceService remEvidenceService = TestResources.getRemEvidenceService();

        // Creates the sample REMEvidenceType
        SignedRemEvidence signedRemEvidence = TestResources.createSampleRemEvidence();

        // Creates the transformer
        RemEvidenceTransformer remEvidenceTransformer = new RemEvidenceTransformer();

        // where to place the transformed output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // performs the actual transformation into XML representation
        remEvidenceTransformer.toFormattedXml(signedRemEvidence, baos);

        System.out.println(baos.toString());

        // Attempts to parse the XML transformed REMEvidence, signature verification should fail
        // as the XML is formatted
        SignedRemEvidence remEvidence = remEvidenceTransformer.parse(new ByteArrayInputStream(baos.toByteArray()));

        assertNotNull(remEvidence);

        try {
            XmldsigVerifier.verify(remEvidence.getDocument());
            fail("Tthe formatted xml should not constitute a valid signature");
        } catch (PeppolSecurityException e) {
            // This is expected
        }
    }

    /**
     * Creates sample REM Evidence, transforms it into XML representation and
     * parses it back into a Java object again.
     * @throws Exception
     */
    @Test
    public void verifyRoundTrip() throws Exception {
        // Obtains instance of the service which is the entry point to the Rem package
        RemEvidenceService remEvidenceService = TestResources.getRemEvidenceService();

        // Creates the sample REMEvidenceType
        SignedRemEvidence signedRemEvidence = TestResources.createSampleRemEvidence();

        // Transforms evidence to XML representation
        RemEvidenceTransformer remEvidenceTransformer = new RemEvidenceTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        remEvidenceTransformer.setFormattedOutput(false);
        remEvidenceTransformer.toUnformattedXml(signedRemEvidence,baos);

        // Transforms back again....
        SignedRemEvidence remEvidence = remEvidenceTransformer.parse(new ByteArrayInputStream(baos.toByteArray()));

        // Signature should still verify
        RemEvidenceService.verifySignature(remEvidence);


    }
}