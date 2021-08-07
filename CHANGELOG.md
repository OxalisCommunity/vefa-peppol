# Changelog

## Next release

_Stay tuned._

## 2.0.2
* Bump httpclient from 4.5.12 to 4.5.13 : d9e38c457d1d2a9baa17ffdb338906327a7ca25d
* Added codeql for code scanning  : f7580c4ae6b5b46a56e607d6b12461e053367c2c
* Guava security vulnerability fix for CVE-2020-8908 : 4ad3da8551d8859b02330349450a7f24d6194bee

## 2.0.1
* Addition of new approved ICD values - OpenPEPPOL eDEC Code Lists - Participant Identifier Schemes v7.5 : faa49b4daf450fb89a05424ffbb39eac7e768935 
* SMP-Redirect by @FrodeBjerkholt : ddda09aa89596a18ffbec18f0d2293a889e37683  👏
* Invalid Null Check - Exception in BusdoxReader.java  : 84fd271aecbeb5accbfbf9bc1c98639e7f3122e4

## 2.0.0
* Organizational changes to project - GroupID, Package name refactoring etc : 1bf317f and b6778e9
* Addition of new approved ICD values - OpenPEPPOL eDEC Code Lists - Participant Identifier Schemes v7.3 : c20fdc2
* Change in default lookup behavior to CNAME (as per Peppol SML specification) : f710ad3
* NotFoundException :  #31 by ron-dan (https://github.com/ron-dan)  👏

## 1.0.4

* Throwing explicit exceptions in SbdhReader when information is not provided.
* Fixing functionality in peppol-publisher.
* Updated list of ICDs.


## 1.0.3

* Fixing issue after fixing [#23](https://github.com/difi/vefa-peppol/issues/23). Added test.


## 1.0.2

* Keeping attribute prefix while copying XML streams. [#23](https://github.com/difi/vefa-peppol/issues/23)
* Updating model to allow for multiple process identifiers as part of one process metadata.
* Adding concept of service reference in lookup.
* Adding issuing agencies for ICDs.


## 1.0.1

* Adding support for next generation of PEPPOL PKI.
* Updating list of ICDs used by PEPPOL.
* Adding better exceptions when lookup fails.


## 1.0.0

First major release of this library.
