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


Display Credentials in Repository
---------------------------------

A user can view a table containing all of the credentials stored in the
repository.

* Table columns must include the short name for the credential and the
  date the credential's certificate expires.


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
    
First Time User
---------------

When an authorized user visits the application for the first time, a welcome
screen is displayed.

* The welcome screen displays some helpful information about the purpose
  of the application.
* The user can activate a button/link to create a user profile.


Create a User Profile
---------------------

An authorized user can create a user profile.  The profile is created on the
user's first visit to the application.

A user profile contains
* username --- the same string used to access the application; this field
  is displayed in the form, but is set to the logged-in user name and is
  
* full name --- used in displaying current activity, etc
* password --- used to protect the user's private key; depending on the 
  access control mechanism, this may also be the login password
* public/private key pair -- used to access stored credentials; this key pair
  is created and stored for the user but the user doesn't need to know about it

* If the user cancel's without creating a profile, he/she is returned to the
  welcome screen.
* It must not be possible to create a user profile for anyone other than the
  authentic, logged-in user.


Display New Password Complexity
---------------------------------

When entering a password for that will be assigned to her profile, the user
interface provides a simple subjective and visual indication of password 
complexity that is updated as the user types the password.

* e.g. Weak (red), Moderate (yellow), Strong (green)


"Self" Group for a User
-----------------------

When a user creates her profile she is automatically added to a "self" group 
that contains no other users.

* The user is always a member of the "self" group; i.e. she cannot be removed
  from this group.


Ownership of an Imported Credential
-----------------------------------

When a user imports a credential, the ownership of the credential is assigned
to "self".


Encrypt the Private Key of an Imported Credential
-------------------------------------------------

When importing a credential, the private key is encrypted using the
secret key associated with the group that is assigned as the credential's
owner.

  
Decrypt Private Key on Credential Export
----------------------------------------

When exporting a credential, the private key is decrypted (using the owner
group's secret key).

* The user's password is required to decrypt the group's secret key.



    
 
