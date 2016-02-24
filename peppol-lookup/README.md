# VEFA PEPPOL Lookup

Functionality for looking up participants in PEPPOL.


## Features

* Supported SML lookup:
  * [PEPPOL SML](https://joinup.ec.europa.eu/svn/peppol/PEPPOL_EIA/1-ICT_Architecture/1-ICT-Transport_Infrastructure/13-ICT-Models/ICT-Transport-SML_Service_Specification-101.pdf)
  * [OASIS BDXL](http://docs.oasis-open.org/bdxr/BDX-Location/v1.0/BDX-Location-v1.0.html)
  * Disabled (static)
* Supported SMP lookup:
  * [PEPPOL SMP](https://joinup.ec.europa.eu/svn/peppol/PEPPOL_EIA/1-ICT_Architecture/1-ICT-Transport_Infrastructure/13-ICT-Models/ICT-Transport-SMP_Service_Specification-110.pdf)
  * [OASIS BDXR SMP](http://docs.oasis-open.org/bdxr/bdx-smp/v1.0/bdx-smp-v1.0.html)
* Validates signatures.
* Uses [peppol-security](/peppol-security) to verify certificates.
* API for extensions.


## Getting started

Include dependency in your pom.xml:

```xml
<dependency>
	<groupId>no.difi.vefa</groupId>
	<artifactId>peppol-lookup</artifactId>
	<version>0.9.5</version>
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
