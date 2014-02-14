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

Display Credentials in Repository
---------------------------------

A user can view a table containing all of the credentials stored in the
repository.

* Table columns must include the short name for the credential and the
  date it was added or last modified.

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
    * Put the CA certificates a file named server-ca.crt.
* If the private key is protected, allow the user to indicate that the 
  private key should be exported in unprotected form in the ZIP archive.
  Prompt the user to enter the passphrase for the private key if this option 
  is selected. 



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




 
  