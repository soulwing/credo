"Self" Group for a User
-----------------------

When a user creates her profile she is automatically added to a "self" group 
that contains no other users.

* The user is always a member of the "self" group; i.e. she cannot be removed
  from this group.


Ownership of an Imported Credential
-----------------------------------

When a user imports a credential, the ownership of the credential is assigned.

* If the user is a member of no groups other than "self", the ownership of
  the credential is assigned to "self"; the user is not presented with any
  option to change the ownership.
* If the user is a member of at least one group other than "self", the user
  can choose a group for the credential's ownership.
  
  
Export Credential in Common Key Store Format
--------------------------------------------

When exporting a credential, the user can choose a key store format as an
alternative to receiving the credential as a ZIP file containing PEM-encoded
files.

* Supported key store formats should include PKCS12, JKS, and BKS, as well as 
  the concatenated PEM files format used by curl.


Encrypt the Private Key of an Imported Credential
-------------------------------------------------

When importing a credential, the private key is encrypted using the
secret key associated with the group that is assigned as the credential's
owner.






 
  