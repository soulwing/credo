credo
=====

A web application that manages a collection of X.509 credentials.

Features
--------

* Secure archival storage of certificate, corresponding private key, and CA
  certificate chain
* Easy import and export of existing credentials using standard/conventional
  file formats (e.g. PKCS12, DER, PEM)
* Automatically fill in details for the record of a credential from attributes
  of the associated certificate
* Easily replace expired/revoked credentials and maintain history of all 
  previously assigned credentials
* Apply tags to each credential to organize, group, and easily identify 
  credentials


What Makes Credo Secure?
------------------------

The architecture of Credo is based on well-established public-key cryptography
practices. Much of Credo's architecture was influenced by the architecture of
OpenPGP.

User Profiles
-------------

Credo's security model starts at the _User Profile_ associated with a Credo 
user.  When a user creates her profile, Credo generates an RSA key pair and 
stores it in the profile. The associated private key is encrypted with a 
user-provided password, so that a person with access to the underlying database 
cannot utilize the private key. 

Credo _does not store the user's password in the database_.  However, it does 
store a one-way hash of the user's password for use in validating an input 
password when requested from the user. Credo uses the same _salted_, _iterated_, 
SHA-2 password hash mechanism utilized by modern operating systems. This 
protects the password from brute force attacks by a person who gains access to 
Credo's underlying database. 

Groups
------

Credo uses the concept of _groups_ to allow a group of users to share access to 
a stored credential. For each group, Credo generates an AES (symmetric) key 
that will be used to encrypt credentials that are owned by the group. _No 
unencrypted copy of a group's AES key is stored in the database_.

For each member of a group, Credo encrypts the group's AES key using the user's 
public RSA key. The encryption of the group's AES key utilizes a standard 
key wrapping procedure using RSA with _Optimal Asymmetric Encryption Padding_ 
(OAEP) and a SHA-2 hash.
 
The AES key for a group can be decrypted using the private RSA key of any 
group member. In order to access the user's private RSA key, Credo prompts the 
user to enter her password whenever a group's AES key is needed; the password is 
used to decrypt the user's private key and is subsequently discarded. 

Credo allows groups to be organized in a hierarchy. Every group can specify
a parent group. The members of a group include those users explicitly listed
as members as well as those users who are members of any of the group's
ancestors.

For all groups that have a parent, Credo encrypts a copy of the group's AES
key using the AES key of the parent group. When a group's AES key is needed,
and the user is a member of an ancestor group, Credo first decrypts the AES 
key of the group in which the user is a direct member.  It then uses this 
key to decrypt the AES key of next group in the path to the target group. The
resulting AES key is then used to decrypt the key of the next group, and so on, 
until the target group is reached.

Every Credo _User Profile_ includes a special group named _self_. This group 
contains only the user associated with the profile. The _self_ group provides
a convenient mechanism for allowing a single user to be the owner of a stored
credential, without the need to create a group containing that single user. 
No other users can be added to a user's _self_ group.  

Credentials
-----------

Credo uses the term _credential_ to mean the combination of a private key
and a certificate containing the corresponding public key.  Before a 
credential is stored in Credo, the user assigns a group that will own the
credential. The AES key for the group is used to encrypt the credential's
private key. _No unencrypted copy of a credential's private key is stored in
the database_.

A user can assign an owner group to a credential using any group in which she 
is a member. The default owner group for a credential is _self_ which allows
storage of a credential that only the user who created it can access.  The user
can choose another group as desired.  

The user is prompted for her password at the time a credential is stored. The 
password is used to decrypt the user's private RSA key, which is in turn used 
to unwrap (decrypt) her copy of the group's AES key, which is then used to 
encrypt the private key associated with the credential before it is stored.

The owner group for a credential can be changed by any member of the group at
any time after the credential has been stored. The user can choose any group
in which she is a member as the new owner group. The user will be prompted for
her password. The password is used to unwrap the user's copy of the AES key
for the current owner group. This key is then used to decrypt the private key
for the credential. The password is then used to unwrap the user's copy of the
AES key for the new owner group. The new owner group's AES key is then used to
re-encrypt the credential's private key and store it. This entire procedure is
carried out in a single transaction so ensure that the database record of the
credential's owner group and the encrypted private key are consistent.

Credential Requests
-------------------

In Credo, the term _credential request_ is used to mean the combination of a
private key and a standard X.509 Certificate Signing Request object.  When a
user creates a credential request, Credo generates a private key and then uses
it to create a certificate signing request for a user-specified subject name.

A credential request is assigned an owner group. Credo uses the AES key of this
group to encrypt the private key associated with the request, just as it does
for credentials. _No unencrypted copy of a credential request's private key 
is stored in the database_.


Wildfly Setup Notes
-------------------

```
/subsystem=datasources/data-source=credo:add(
  jndi-name=java:/jdbc/datasources/credo, 
  use-ccm=false, 
  connection-url=jdbc:postgresql://localhost/credo,
  driver-name=postgresql-9.4-1201-jdbc41.jar, user-name=credo, 
  password=credo)
```