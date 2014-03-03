Encrypt the Private Key of an Imported Credential
-------------------------------------------------

When importing a credential, the private key is encrypted using the
secret key associated with the group that is assigned as the credential's
owner.

  
Export Credential in Common Key Store Format
--------------------------------------------

When exporting a credential, the user can choose a key store format as an
alternative to receiving the credential as a ZIP file containing PEM-encoded
files.

* Supported key store formats should include PKCS12, JKS, and BKS, as well as 
  the concatenated PEM files format used by curl.


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





 
  