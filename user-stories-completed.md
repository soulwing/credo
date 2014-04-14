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

Protect Private Key of Exported Credential
------------------------------------------

When exporting a credential, the user can specify a passphrase that will be
used to encrypt the credential's private key.

* If the export format requires a passphrase, the passphrase must be specified
* If the export format does not require a passphrase, and the user chooses not
  to provide one, a warning is issued.
  

Export Credential in Common Key Store Format
--------------------------------------------

When exporting a credential, the user can choose a key store format as an
alternative to receiving the credential as a ZIP file containing PEM-encoded
files.

* Supported key store formats should include PKCS12, JKS, and BKS, as well as 
  the concatenated PEM files format used by curl.


Password Entry Feedback
-----------------------

When the user is required to enter her password, the password field displays 
a visual indicator when the correct password is entered, without the need to
submit the form.


Restricted Credential Table View
--------------------------------

A user who navigates to the Credential Table View will see only those 
credentials for which the user is an owner.

* A user is an owner of a credential if she is a member of group that owns
  the credential.


Form Field Focus
----------------

When a form is displayed, the user does not need to click to draw focus to
the first field.

* Assign focus to the first input field in the form.


Generate Export Passphrase
--------------------------

When exporting a credential the user can choose to generate a random
export passphrase.


Create New Group
----------------

A user can create a new group.

* The user who creates the group becomes the group's owner.
* The user must specify the name of the group and may specify a description
  for the group
* The owner is always a member of the group (at least in the sense that the 
  owner can decrypt the group's secret key using her own private key)
* The user can choose zero or more additional group members before saving the
  new group.
  

View Groups
-----------

A user can view all groups in which she is a member.


Edit Group
----------

A user can edit an existing group for which she is a member.

* The user can change the name and/or the description for the group, and 
  can add and remove group members as desired (except that she cannot remove
  herself from the group).  


Remove Group
------------

A user can remove a group in which she is a member, provided that the group
is not assigned as the owner of any credential.

* The user should be prompted to confirm before the group is removed.


Assign a Group Owner for an Imported Credential
-----------------------------------------------

When importing a credential, if the user is a member of a group in addition
to "self", she can choose to assign ownership to a group other than "self".

* Only those groups for which the user is a member may be selected as the
  owner of the credential.
  

Create a New Group for an Imported Credential
---------------------------------------------

When importing a credential, a user can create a new group containing herself
by specifying the new group name as the owner.


Disable Delete Control for In-Use Groups
----------------------------------------

The Delete control in the Groups table view should be disabled for any group
that is assigned to one or more credentials.


Owner Group Auto-Create Feedback
--------------------------------

When a user specifies an non-existent group name in the Owner field
of the import credential details form, she should receive feedback that a new 
group will be created.


Auto-Complete Owner Group
-------------------------

As the user types a group name in the Owner field of the import credential
details form, she should be offered auto-complete choices from which she
can optionally choose a full group name.


Auto-Complete Credential Tags
-----------------------------

As the user types a tag name in the Tags field of the import credential
details form, she should be offered auto-complete choices from which she can
optionally choose a tag.


Logged In User
--------------

A user sees her full name as the logged-in user in the top navigation bar.
 

Remove Credential
-----------------

A user can remove a credential if she is a member of the group that owns the
credential.

* The user should be prompted to confirm before the credential is removed.


Signing Request for Existing Credential
---------------------------------------

A user can create a signing request to renew an existing credential.

* The subject name for the request is the same as the subject name on the
  credential.
* The friendly name, owner, and description, and tags should be copied from 
  the existing credential, and the user should have the opportunity to edit 
  them before saving the request.


View Signing Requests
---------------------

A user can view a table of signing requests for which she is an owner.

* It should be a table view with the friendly name, issuer name, date 
  created, and a flag that indicates whether the request has been completed
  (i.e. whether it has been used to create a credential).
* Each row in the table should include controls to edit or remove the 
  request, and to import the signed certificate chain.
 

Remove Signing Request
----------------------

A user can remove a signing request for which she is an owner.

* The user should be prompted to confirm before the request is removed.
* The user receives a warning if no credential has been created from the
  request.

 
Download Signing Request
------------------------

A user can download the certification request file for an existing signing
request for which she is an owner.


Create Credential from Signing Request
--------------------------------------

A user can create a credential from an existing signing request for which
she is an owner.

* The subject certificate and authority chain must be uploaded and validated.
* The friendly name, owner, description, and tags should be copied from the
  request, and the user should be given the opportunity to edit them.
* The user should be given the option to delete the signing request upon 
  successful creation of the credential.
* If the user chooses not to remove the signing request, it should be flagged
  as "complete" upon successful creation of the credential.


Remove Renewed Credential
-------------------------

After successfully importing the signed certificate for a request that renews
an existing credential, the user should be offered the option of removing the
old credential.


Edit Credential Signing Request
-------------------------------

A user can edit an existing signing request for which she is an owner.

* The friendly name, description, and tags, and owner can be changed.
* None of the attributes of the X.509 signing request object can be changed.


Edit Credential
---------------

A user can edit an existing credential for which she is an owner.

* The name, note, and tags can be edited.
* The owner group can be changed to another group of which she is a member.


Signing Request for New Credential
----------------------------------

A user can create a signing request for a new credential.

* A subject common name must be specified.
* A friendly name must be specified; the default value is the subject common
  name
* An owner must be specified
* A description and zero or more tags may be specified





    
 
