# VEFA PEPPOL Security

Security features for PEPPOL.


## Features

* Verifies XMLDSig.
* Contains PEPPOL certificates for TEST and PRODUCTION.
* Uses [Certificate Validator](https://github.com/difi/certvalidator) to validate certificates.


## Getting started

Include dependency in your pom.xml:

```xml
<dependency>
	<groupId>no.difi.vefa</groupId>
	<artifactId>peppol-security</artifactId>
	<version>0.9.5</version>
</dependency>
```