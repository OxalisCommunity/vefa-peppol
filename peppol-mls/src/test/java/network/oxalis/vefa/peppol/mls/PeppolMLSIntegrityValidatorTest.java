/*
 * Copyright 2015-2026 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package network.oxalis.vefa.peppol.mls;

import network.oxalis.peppol.ubl2.jaxb.ApplicationResponseType;
import network.oxalis.peppol.ubl2.jaxb.cac.*;
import network.oxalis.peppol.ubl2.jaxb.cbc.*;
import org.testng.annotations.Test;

import java.util.UUID;

public class PeppolMLSIntegrityValidatorTest {
    private ApplicationResponseType createValidMLS() {

        ApplicationResponseType mls = new ApplicationResponseType();

        CustomizationIDType customizationID = new CustomizationIDType();
        customizationID.setValue("urn:test");
        mls.setCustomizationID(customizationID);

        ProfileIDType profileID = new ProfileIDType();
        profileID.setValue("urn:test-profile");
        mls.setProfileID(profileID);

        IDType id = new IDType();
        id.setValue(UUID.randomUUID().toString());
        mls.setID(id);

        IssueDateType issueDate = new IssueDateType();
        issueDate.setValue(javax.xml.datatype.DatatypeFactory.newDefaultInstance()
                .newXMLGregorianCalendarDate(2024, 1, 1, 0));
        mls.setIssueDate(issueDate);

        IssueTimeType issueTime = new IssueTimeType();
        issueTime.setValue(javax.xml.datatype.DatatypeFactory.newDefaultInstance()
                .newXMLGregorianCalendarTime(10, 0, 0, 0));
        mls.setIssueTime(issueTime);

        mls.setSenderParty(createValidParty());
        mls.setReceiverParty(createValidParty());

        DocumentResponseType dr = new DocumentResponseType();

        DocumentReferenceType docRef = new DocumentReferenceType();
        IDType refId = new IDType();
        refId.setValue("doc-123");
        docRef.setID(refId);
        dr.getDocumentReference().add(docRef);

        ResponseType response = new ResponseType();
        ResponseCodeType responseCode = new ResponseCodeType();
        responseCode.setValue("AP");
        response.setResponseCode(responseCode);
        dr.setResponse(response);

        mls.getDocumentResponse().add(dr);

        return mls;
    }

    private PartyType createValidParty() {
        PartyType party = new PartyType();
        EndpointIDType endpoint = new EndpointIDType();
        endpoint.setValue("123456789");
        endpoint.setSchemeID("0242");
        party.setEndpointID(endpoint);
        return party;
    }

    @Test
    public void testValidMLSShouldPassValidation() {
        ApplicationResponseType mls = createValidMLS();
        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test(expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = "MLS cannot be null")
    public void testNullMLSShouldFail() {
        PeppolMLSIntegrityValidator.validate(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingCustomizationIDShouldFail() {
        ApplicationResponseType mls = createValidMLS();
        mls.setCustomizationID(null);
        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingSenderPartyShouldFail() {
        ApplicationResponseType mls = createValidMLS();
        mls.setSenderParty(null);
        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMultipleDocumentResponsesShouldFail() {
        ApplicationResponseType mls = createValidMLS();
        mls.getDocumentResponse().add(new DocumentResponseType());
        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingDocumentReferenceShouldFail() {
        ApplicationResponseType mls = createValidMLS();
        mls.getDocumentResponse().get(0).getDocumentReference().clear();
        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingDocumentLevelResponseShouldFail() {
        ApplicationResponseType mls = createValidMLS();
        mls.getDocumentResponse().get(0).setResponse(null);
        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMultipleDocumentDescriptionsShouldFail() {
        ApplicationResponseType mls = createValidMLS();
        ResponseType response = mls.getDocumentResponse().get(0).getResponse();

        DescriptionType d1 = new DescriptionType();
        d1.setValue("Desc1");

        DescriptionType d2 = new DescriptionType();
        d2.setValue("Desc2");

        response.getDescription().add(d1);
        response.getDescription().add(d2);

        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testLineResponseWithoutResponseShouldFail() {
        ApplicationResponseType mls = createValidMLS();

        LineResponseType lineResponse = new LineResponseType();

        LineReferenceType lineReference = new LineReferenceType();
        LineIDType lineID = new LineIDType();
        lineID.setValue("1");
        lineReference.setLineID(lineID);

        lineResponse.setLineReference(lineReference);

        mls.getDocumentResponse().get(0)
                .getLineResponse().add(lineResponse);

        PeppolMLSIntegrityValidator.validate(mls);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testLineResponseWithMultipleDescriptionsShouldFail() {
        ApplicationResponseType mls = createValidMLS();

        LineResponseType lr = new LineResponseType();

        LineReferenceType ref = new LineReferenceType();
        LineIDType lineID = new LineIDType();
        lineID.setValue("1");
        ref.setLineID(lineID);
        lr.setLineReference(ref);

        ResponseType r = new ResponseType();

        DescriptionType d1 = new DescriptionType();
        d1.setValue("A");
        DescriptionType d2 = new DescriptionType();
        d2.setValue("B");

        r.getDescription().add(d1);
        r.getDescription().add(d2);

        StatusType status = new StatusType();
        StatusReasonCodeType code = new StatusReasonCodeType();
        code.setValue("BV");
        status.setStatusReasonCode(code);

        r.getStatus().add(status);
        lr.getResponse().add(r);

        mls.getDocumentResponse().get(0).getLineResponse().add(lr);

        PeppolMLSIntegrityValidator.validate(mls);
    }
}

