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
import network.oxalis.vefa.peppol.mls.util.UblDateTimeUtil;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public final class PeppolMLSBuilder {

    private final ApplicationResponseType mls = new ApplicationResponseType();
    private final DocumentResponseType documentResponse = new DocumentResponseType();

    private Supplier<String> idMLS = () -> UUID.randomUUID().toString();
    private Supplier<LocalDate> issueDateMLS = LocalDate::now;
    private Supplier<OffsetTime> issueTimeMLS = OffsetTime::now;

    private boolean documentResponseSet = false;
    private boolean built = false;

    public PeppolMLSBuilder(
            String senderId,
            String senderScheme,
            String receiverId,
            String receiverScheme) {

        validateNotBlank(senderId, "Sender ID is required");
        validateNotBlank(senderScheme, "Sender schemeID is required");
        validateNotBlank(receiverId, "Receiver ID is required");
        validateNotBlank(receiverScheme, "Receiver schemeID is required");

        setBasicMLSProfile(PeppolMLSProfile.MLS_1_0);

        setSender(senderId, senderScheme);
        setReceiver(receiverId, receiverScheme);

        // Enforce single DocumentResponse
        mls.getDocumentResponse().clear();
        mls.getDocumentResponse().add(documentResponse);
    }

    private void setBasicMLSProfile(PeppolMLSProfile profile) {

        if (profile != PeppolMLSProfile.MLS_1_0) {
            throw new IllegalStateException("Unsupported MLS profile version");
        }

        CustomizationIDType customizationID = new CustomizationIDType();
        customizationID.setValue(profile.getCustomizationId());
        mls.setCustomizationID(customizationID);

        ProfileIDType profileID = new ProfileIDType();
        profileID.setValue(profile.getProfileId());
        mls.setProfileID(profileID);

    }

    public PeppolMLSBuilder withIdMLS(Supplier<String> idMLS) {
        this.idMLS = idMLS;
        return this;
    }

    public PeppolMLSBuilder withIssueDateMLS(Supplier<LocalDate> issueDateMLS) {
        this.issueDateMLS = issueDateMLS;
        return this;
    }

    public PeppolMLSBuilder withIssueTimeMLS(Supplier<OffsetTime> issueTimeMLS) {
        this.issueTimeMLS = issueTimeMLS;
        return this;
    }

    private void initializeMetadata() {

        IDType id = new IDType();
        id.setValue(idMLS.get());
        mls.setID(id);

        IssueDateType issueDate = new IssueDateType();
        issueDate.setValue(UblDateTimeUtil.toXmlDate(issueDateMLS.get()));
        mls.setIssueDate(issueDate);

        IssueTimeType issueTime = new IssueTimeType();
        issueTime.setValue(UblDateTimeUtil.toXmlTimeWithOffset(issueTimeMLS.get()));
        mls.setIssueTime(issueTime);
    }

    private void assertNotBuilt() {
        if (built) {
            throw new IllegalStateException("Builder already used. It is immutable after build().");
        }
    }

    private void setSender(String senderId, String scheme) {

        PartyType sender = new PartyType();
        EndpointIDType endpoint = new EndpointIDType();
        endpoint.setValue(senderId);
        endpoint.setSchemeID(scheme);

        sender.setEndpointID(endpoint);
        mls.setSenderParty(sender);
    }

    private void setReceiver(String receiverId, String scheme) {

        PartyType receiver = new PartyType();
        EndpointIDType endpoint = new EndpointIDType();
        endpoint.setValue(receiverId);
        endpoint.setSchemeID(scheme);

        receiver.setEndpointID(endpoint);
        mls.setReceiverParty(receiver);
    }

    public PeppolMLSBuilder setDocumentResponse(
            String originalSbdhInstanceIdentifier,
            String responseCode,
            String description) {

        assertNotBuilt();

        if (documentResponseSet) {
            throw new IllegalStateException("Exactly one DocumentResponse is allowed");
        }

        validateNotBlank(originalSbdhInstanceIdentifier,
                "DocumentReference ID is required");

        validateNotBlank(responseCode,
                "ResponseCode is required");

        ResponseType responseType = new ResponseType();

        ResponseCodeType responseCodeType = new ResponseCodeType();
        responseCodeType.setValue(responseCode);
        responseType.setResponseCode(responseCodeType);

        if (description != null && !description.isBlank()) {
            DescriptionType descriptionType = new DescriptionType();
            descriptionType.setValue(description);
            responseType.getDescription().add(descriptionType);
        }

        documentResponse.setResponse(responseType);

        documentResponse.getDocumentReference().clear();

        DocumentReferenceType documentReference = new DocumentReferenceType();
        IDType id = new IDType();
        id.setValue(originalSbdhInstanceIdentifier);
        documentReference.setID(id);

        documentResponse.getDocumentReference().add(documentReference);

        documentResponseSet = true;

        return this;
    }


    public PeppolMLSBuilder addLineResponse(LineResponseType lineResponse) {

        assertNotBuilt();

        if (lineResponse == null) {
            throw new IllegalArgumentException("LineResponse cannot be null");
        }

        documentResponse.getLineResponse().add(lineResponse);
        return this;
    }

    public PeppolMLSBuilder addLineResponses(List<LineResponseType> lineResponses) {

        assertNotBuilt();

        if (lineResponses == null) {
            throw new IllegalArgumentException("LineResponses list cannot be null");
        }

        lineResponses.forEach(this::addLineResponse);
        return this;
    }

    public ApplicationResponseType build() {

        assertNotBuilt();

        if (!documentResponseSet) {
            throw new IllegalStateException("DocumentResponse must be set");
        }

        initializeMetadata();

        PeppolMLSIntegrityValidator.validate(mls);

        built = true;

        return deepCopy(mls);
    }

    private ApplicationResponseType deepCopy(ApplicationResponseType source) {
        // JAXB defensive clone via marshalling could be used.
        // Here simplified shallow defensive copy (safe enough if builder is locked).
        ApplicationResponseType copy = new ApplicationResponseType();
        copy.setCustomizationID(source.getCustomizationID());
        copy.setProfileID(source.getProfileID());
        copy.setID(source.getID());
        copy.setIssueDate(source.getIssueDate());
        copy.setIssueTime(source.getIssueTime());
        copy.setSenderParty(source.getSenderParty());
        copy.setReceiverParty(source.getReceiverParty());
        copy.getDocumentResponse().addAll(source.getDocumentResponse());
        return copy;
    }

    private void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
