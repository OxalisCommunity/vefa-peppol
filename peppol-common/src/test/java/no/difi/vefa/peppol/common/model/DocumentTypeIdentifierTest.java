package no.difi.vefa.peppol.common.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DocumentTypeIdentifierTest {

    @Test
    public void simple() {
        String documentIdentifier = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1";
        assertEquals(new DocumentTypeIdentifier(documentIdentifier).getIdentifier(), documentIdentifier);
    }

}
