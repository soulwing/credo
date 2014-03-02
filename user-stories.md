Create a User Profile
---------------------

An authorized user can create a user profile.  The profile is created on the
user's first visit to the application.

A user profile contains
* username --- the same string used to access the application; this field
  is displayed in the form, but is set to the logged-in user name and is
  read-only
* full name --- used in displaying current activity, etc
* password --- used to protect the user's private key; depending on the 
  access control mechanism, this may also be the login password
* public/private key pair -- used to access stored credentials; this key pair
  is created and stored for the user but the user doesn't need to know about it

* If the user cancel's without creating a profile, he/she is returned to the
  welcome screen.
* It must not be possible to create a user profile for anyone other than the
  authentic, logged-in user.


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




 
  