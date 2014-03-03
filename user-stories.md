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




 
  