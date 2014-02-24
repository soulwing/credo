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
  

Export Credential in Common Key Store Format
--------------------------------------------

When exporting a credential, the user can choose a key store format as an
alternative to receiving the credential as a ZIP file containing PEM-encoded
files.

* Supported key store formats should include PKCS12, JKS, and BKS, as well as 
  the concatenated PEM files format used by curl.


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




 
  