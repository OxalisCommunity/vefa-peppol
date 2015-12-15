[![Build Status](https://travis-ci.org/difi/vefa-peppol.svg?branch=master)](https://travis-ci.org/difi/vefa-peppol)

# VEFA PEPPOL

## Getting started with peppol-lookup

Include dependency in your pom.xml:

```xml
<dependency>
	<groupId>no.difi.vefa</groupId>
	<artifactId>peppol-lookup</artifactId>
	<version>0.9.3</version>
</dependency>
```

Start making lookups:

```java
// Prepare an instance of the client.
LookupClient client = LookupClientBuilder.forProduction().build();

// Fetch document identifiers supported by a participant.
List<DocumentIdentifier> documentTypeIdentifiers = client.getDocumentIdentifiers(
    new ParticipantIdentifier("9908:991825827")
);

// Fetch endpoint directly.
Endpoint endpoint = client.getEndpoint(
    new ParticipantIdentifier("9908:991825827"),
    new DocumentIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1"),
    new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver2.0"),
    TransportProfile.AS2_1_0
);

// Fetch service metadata for a given participant and document identifier, then endpoint.
ServiceMetadata serviceMetadata = client.getServiceMetadata(
    new ParticipantIdentifier("9908:991825827"),
    new DocumentIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1")
);
Endpoint endpoint = client.getEndpoint(
    serviceMetadata,
    new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver2.0"),
    TransportProfile.AS2_1_0
);
// Note: fetching endpoint directly from service metadata doesn't involve validation of endpoint certificate.
```

## Getting started with peppol-evidence

Please see [README file for peppol-evidence](peppol-evidence/README.md)
