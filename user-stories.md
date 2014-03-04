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


Restricted Credential Table View
--------------------------------

A user who navigates to the Credential Table View will see only those 
credentials for which the user is an owner.

* A user is an owner of a credential if she is a member of group that owns
  the credential.
  
 
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


Password Entry Feedback
-----------------------

When the user is required to enter her password, the password field displays 
a visual indicator when the correct password is entered, without the need to
submit the form.




 
  