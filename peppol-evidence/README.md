# VEFA PEPPOL Evidence

This module contains an implementation of the ETSI TS 102 640-2 V2.1.1 also known as "REM".

The XML Schema originally provided by ETSI has been modified:

 * Slight adjustments made by Jörg Apitzsch
 * Inclusion of protocol specific transport evidence represented by for instance PEPPOL AS2 MDN or AS4 Soap headers in the ```Extensions``` element. 
   
    **NOTE!** The transport protocol evidence is included as a stream of Base64 bytes in order to preserve white space etc., which must not be changed in order to preserve any
    digital signatures.
   


## Getting started

Include dependency in your pom.xml:

```xml
<dependency>
	<groupId>no.difi.vefa</groupId>
	<artifactId>peppol-evidence</artifactId>
	<version>0.9.5</version>
</dependency>
```

The following example is an extract from [RemEvidenceBuilderTest](src/test/java/no/difi/vefa/peppol/evidence/rem/RemEvidenceBuilderTest.java)

```java
RemEvidenceService remEvidenceService = new RemEvidenceService();
RemEvidenceBuilder builder = remEvidenceService.createDeliveryNonDeliveryToRecipientBuilder();
builder.eventCode(EventCode.ACCEPTANCE)
        .eventTime(new Date())
        .eventReason(EventReason.OTHER)
        .senderIdentifier(TestResources.SENDER_IDENTIFIER)
        .recipientIdentifer(TestResources.RECIPIENT_IDENTIFIER)
        .documentTypeId(TestResources.DOC_TYPE_ID)
        .instanceIdentifier(TestResources.INSTANCE_IDENTIFIER)
        .payloadDigest("ThisIsASHA256Digest".getBytes())
        .protocolSpecificEvidence(TransmissionRole.C_3, TransportProfile.AS2_1_0, specificReceiptBytes)
;

// Signs and builds the REMEvidenceType instance
SignedRemEvidence signedRemEvidence = builder.buildRemEvidenceInstance(privateKeyEntry);
```


## Understanding evidence


### The purpose of REM

The purpose of REM is to act as evidence that a certain event has occured during message transfer.
 
The standard is very old, almost archaic and as such needed some adjustements for usage in a 4-corner transport model.


### Providing the evidence

In AS2 the signed MDN is included in the "Extensions" of the REM Evidence
 

### Inclusion of transport specific receipt

Every transport protocol has some sort of receipt created on the receiving side and returned synchronously to the 
sending party.

However these specific receipts are tightly bound to the underlying transport protocol and are hence not suitable
for publication to neither the issuer of the original message, nor the final destination of the message.

This implementation will henceforth include the transport protocol specific receipt inside the REMEvidence using
the extensions mechanism.


### AS2 message delivery notification (MDN)

For AS2 it is recommended that the entire S/MIME message, including the headers, be included inside the REM evidence 
 in order to provide an unbroken chain of evidence from C3 (receiver) to C2 (sender), which may be provided to 
 the original issuer of the business message (C1).
 
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<DeliveryNonDeliveryToRecipient xmlns="http://uri.etsi.org/02640/v2#" xmlns:ns2="http://uri.etsi.org/02231/v2#"
                                xmlns:ns3="http://www.w3.org/2000/09/xmldsig#"
                                xmlns:ns4="http://uri.etsi.org/01903/v1.3.2#"
                                xmlns:ns5="urn:oasis:names:tc:SAML:2.0:assertion"
                                xmlns:ns7="http://peppol.eu/xsd/ticc/receipt/1.0" version="1"
                                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                xsi:schemaLocation="
                    http://uri.etsi.org/01903/v1.3.2# http://uri.etsi.org/01903/v1.3.2/XAdES.xsd
                    urn:oasis:names:tc:SAML:2.0:assertion http://docs.oasis-open.org/security/saml/v2.0/saml-schema-assertion-2.0.xsd
                    http://www.w3.org/2001/04/xmlenc# ">
    <EventCode>http:uri.etsi.org/02640/Event#Acceptance</EventCode>
    <EvidenceIdentifier>8f381623-9e46-4932-a7ec-fd0d0e1b52f5</EvidenceIdentifier>
    <EventTime>2015-11-07T21:41:15.201+01:00</EventTime>
    <SenderDetails>
        <AttributedElectronicAddress scheme="iso6523-actorid-upis">9908:810017902</AttributedElectronicAddress>
    </SenderDetails>
    <RecipientsDetails>
        <EntityDetails>
            <AttributedElectronicAddress scheme="iso6523-actorid-upis">9908:123456789</AttributedElectronicAddress>
        </EntityDetails>
    </RecipientsDetails>
    <SenderMessageDetails isNotification="false">
        <MessageSubject>
            urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1
        </MessageSubject>
        <UAMessageIdentifier>ca59516a-eff0-448f-81d1-245a7d04d190</UAMessageIdentifier>
        <ns3:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/>
        <ns3:DigestValue>VGhpc0lzQVNIQTI1NkRpZ2VzdA==</ns3:DigestValue>
    </SenderMessageDetails>
    <Extensions>
        <Extension>
            <ns4:Any>
                <ns7:PeppolRemExtension>
                    <ns7:TransmissionProtocol>AS2</ns7:TransmissionProtocol>
                    <ns7:TransmissionRole>C3</ns7:TransmissionRole>
                    <ns7:OriginalReceipt>
                        TUlNRS1WZXJzaW9uOiAxLjANCkNvbnRlbnQtVHogbXVsdG ...... AS2 MDN in base64, truncated for readability
                    </ns7:OriginalReceipt>
                </ns7:PeppolRemExtension>
            </ns4:Any>
        </Extension>
    </Extensions>
    <ns3:Signature>
        <ns3:SignedInfo>
            <ns3:CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
            <ns3:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/>
            <ns3:Reference URI="">
                <ns3:Transforms>
                    <ns3:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
                </ns3:Transforms>
                <ns3:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
                <ns3:DigestValue>/zPrxCvj+wkLJ8BR7BJQHMzM7mM=</ns3:DigestValue>
            </ns3:Reference>
        </ns3:SignedInfo>
        <ns3:SignatureValue>
            Ly0wUaxv7U4zH1BcHWpj4rtyorRGGRKXBFqMureWRaZbDnYhceAeKHBmA ...... truncated for readability
        </ns3:SignatureValue>
        <ns3:KeyInfo>
            <ns3:X509Data>
                <ns3:X509SubjectName>CN=VEFA Validator self-signed,OU=Unknown,O=Unknown,L=Unknown,ST=Unknown,C=Unknown
                </ns3:X509SubjectName>
                <ns3:X509Certificate>
                    MIIDnTCCAoWgAwIBAgIEZ/LuJzANBgkqhkiG9w0BAQsFADB/MRAwDgYDV ...... truncated for readability
                </ns3:X509Certificate>
            </ns3:X509Data>
        </ns3:KeyInfo>
    </ns3:Signature>
</DeliveryNonDeliveryToRecipient>
```


### Inspecting REM Evidence with embedded AS2 MDN


In [src/test/resources](src/test/resources) you will find the sample files being used in this short tutorial

`sample-signed-formatted-rem.xml` contains a sample REM Evidence with an AS2 MDN embedded within the `<Extensions>` element.

Here are some interesting stuff you can do, given that you extract the 
entire contents inside the `<OriginalReceipt>` element (namespace omitted for brevity) and save it into a file named
`as2-mdn.b64`


Inspecting the contents of the AS2 MDN:
```
 openssl base64 -d -A <as2-mdn.b64 
```

The following command will decode from base64 and verify the signature of the AS2 MDN, leaving the X.509 certificate in `cert.pem` and
the actual text of the MDN in `textdata`
```
openssl base64 -d -A |openssl smime -verify -noverify -signer cert.pem -out textdata
```

To inspect the certificate:
```
openssl x509 -in cert.pem -noout -text
```


### AS4 original receipt (SOAP Headers)
  
  To be done (sorry)
