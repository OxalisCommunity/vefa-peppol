# VEFA PEPPOL

## Getting started

Include dependency in your pom.xml:

```xml
<dependency>
	<groupId>no.difi.vefa</groupId>
	<artifactId>peppol-lookup</artifactId>
	<version>0.9.2</version>
</dependency>
```

Start making lookups:

```java
// Prepare an instance of the client.
LookupClient client = LookupClientBuilder.forProduction().build();

// Fetch document identifiers supported by a participant
List<DocumentIdentifier> documentIdentifiers = client.getDocumentIdentifiers(
    new ParticipantIdentifier("9908:991825827")
);

// Fetch service metadata for a given participant and document identifier
ServiceMetadata serviceMetadata = client.getServiceMetadata(
    new ParticipantIdentifier("9908:991825827"),
    new DocumentIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1")
);
```