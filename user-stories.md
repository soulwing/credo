Add Credential By PEM Import
----------------------------

Add a credential to a repository by importing PEM-encoded file(s) containing 
a private key, corresponding certificate, and CA certificates.

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

Export Credential in ZIP Archive
--------------------------------

Export a selected credential in the repository to PEM-encoded files wrapped 
in a ZIP archive.

* Use the short name of the credential to derive the base name for 
the ZIP archive.
    * e.g. if the subject certificate is myserver.example.com, the ZIP archive 
      name could be myserver-example-com.zip
* Use Apache httpd naming conventions for the component PEM files.
    * Put the private key in a file named server.key
    * Put the subject certificate in a file name server.crt
    * Put the CA certificates a file named server-ca.crt, in order starting 
      with the subject's issuer, then that CA's issuer, etc.
* If the private key is protected, allow the user to indicate that the 
  private key should be exported in unprotected form in the ZIP archive.
  Prompt the user to enter the passphrase for the private key if this option 
  is selected. 


Display Credentials in Repository
---------------------------------

Display a simple table that includes all credentials stored in the repository,
with the ability to sort and filter.

* Columns should include subject short name, validity start and end dates, 
  issuer common name, key length, time of last update, and should be sortable
* Clicking a table row should reveal more details: full subject name, subject
  names of each CA in the chain, serial number, X509v3 extensions and 
  attributes (e.g. key usage, extended usage, etc),
 

Apply Passphrase to Unprotected Private Key
-------------------------------------------

When importing a private key that is not protected by a passphrase, require
the user to enter a passphrase, and use it to protect the private key before
storing it in the database.

* The user must enter the passphrase twice to verify correct entry.


Display New Passphrase Complexity
---------------------------------

When a user enters a new passphrase that will be used to protect a private 
key, provide a simple subjective and visual indication of password complexity
as the user is typing the passphrase.

* e.g. Weak (red), Moderate (yellow), Strong (green)




 
  