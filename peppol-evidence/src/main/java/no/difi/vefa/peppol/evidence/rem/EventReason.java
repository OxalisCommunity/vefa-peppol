/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
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

package no.difi.vefa.peppol.evidence.rem;

/**
 * Event reason identifiers and codes according to Annex D of ETSI TS 102 640-2 V2.1.1
 *
 * @author steinar
 *         Date: 04.11.2015
 *         Time: 21.41
 */
public enum EventReason {

    INVALID_MESSAGE_FORMAT("http:uri.etsi.org/REM/EventReason#InvalidMessageFormat", "1"),
    MALWARE_FOUND("http:uri.etsi.org/REM/EventReason#MalwareFound", "2"),
    INVALID_USER_SIGNATURE("http:uri.etsi.org/REM/EventReason#InvalidUserSignature", "3"),
    USER_CERT_EXPIRED_OR_REVOKED("http:uri.etsi.org/REM/EventReason#UserCertExpiredOrRevoked", "4"),
    POLICY_VIOLATION("http:uri.etsi.org/REM/EventReason#PolicyViolation", "5"),
    R_REMMD_MALFUNCTION("http:uri.etsi.org/REM/EventReason#R_REMMD_Malfunction", "6"),
    R_REMMD_NOT_IDENTIFIED("http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdenified", "7"),
    R_REMMD_UNCREACHABLE("http:uri.etsi.org/REM/EventReason#R_REMMD_Unreachable", "8"),
    S_REMMD_RECEIVED_NO_DELIVERY_INFO_FROM_R_REMMD
            ("http:uri.etsi.org/REM/EventReason#S_REMMD_ReceivedNoDeliveryInfoFromR_REMMD", "9"),
    UNKNOWN_RECIPIENT("http:uri.etsi.org/REM/EventReason#UnknownRecipient", "10"),
    MAILBOX_FULL("http:uri.etsi.org/REM/EventReason#MailboxFull", "11"),
    TECHNICAL_MALFUNCTION("http:uri.etsi.org/REM/EventReason#TechnicalMalfunction", "12"),
    ATTACHMENT_FORMAT_NOT_ACCEPTED("http:uri.etsi.org/REM/EventReason#AttachementFormatNotAccepted", "13"),
    RECIPIENT_REJECTION("http:uri.etsi.org/REM/EventReason#RecipientRejection", "14"),
    RETENTION_PERIOD_EXPIRED("http:uri.etsi.org/REM/EventReason#RetentionPeriodExpired", "15"),
    REGULAR_EMAIL_UNCREACHABLE("http:uri.etXsi.org/REM/EventReason#RegularEmailUnreachable", "16"),
    REGULAR_EMAIL_NON_OPERATIONAL("http:uri.etsi.org/REM/EventReason#RegularEmailNonOperational", "17"),
    REGULAR_EMAIL_REJECTION("http:uri.etsi.org/REM/EventReason#RegularEmailRejection", "18"),
    PRINTING_SYSTEM_UNREACHABLE("http:uri.etsi.org/REM/EventReason#PrintingSystemUnreachable", "19"),
    PRINTING_SYSTEM_NON_OPERATIONAL("http:uri.etsi.org/REM/EventReason#PrintingSystemNonOperational", "20"),
    PRINTING_BUFFER_FULL("http:uri.etsi.org/REM/EventReason#PrintingBufferFull", "21"),
    OTHER("http:uri.etsi.org/REM/EventReason#Other", "22"),;

    private final String details;
    private final String code;

    EventReason(String details, String code) {
        this.details = details;
        this.code = code;
    }

    public String getDetails() {
        return details;
    }

    public String getCode() {
        return code;
    }

    public static EventReason valueForCode(String code) {
        for (EventReason eventReason : values())
            if (eventReason.getCode().equals(code))
                return eventReason;

        throw new IllegalArgumentException(String.format("Code '%s' is not a valid code for EventReason", code));
    }
}
