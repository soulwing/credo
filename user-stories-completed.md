Add Credential By PEM Import
----------------------------

A user can add a credential to the repository by importing its constituent 
parts from one or more PEM-encoded files.

* The key, certificate, and CA certificates may be in any combination of up 
  to five files.
* Allow the user to enter a short name for the certificate
* Allow the user to enter a description of the certificate
* Allow the user to apply any number of tags to the certificate, defining
  new tags as desired

Validate Credential on Import
-----------------------------

When importing a credential, the upload file contents must be validated.

* Do not allow the import if private key does not correspond to the subject
  certificate.
* If the private key is protected by a passphrase, prompt the user to enter
  it, but do not retain it longer than necessary to validate the private key.
* Warn the user if the identity chain is incomplete (does not end on the
  correct self-signed certificate).
* Warn the user if irrelevant CA certificates were included.

