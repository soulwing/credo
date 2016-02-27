credo
=====

A web application that manages a collection of X.509 credentials.

Features
--------

* Secure archival storage of certificates, corresponding private keys, and 
  associated CA certificate chains
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
practices. 

Much of Credo's architecture was influenced by the architecture of OpenPGP.

### Cryptography 101

In order to grasp how and why Credo works, it is important for you to understand
a few fundamental cryptographic concepts.

1. A _key_ is a data structure that can be used with a cryptographic function 
   to either _encrypt_ or _decrypt_ other data.  The nature of the cryptographic
   function and associated key structure determines whether a given key can be 
   used for encryption, decryption, or both.
2. When data is _encrypted_ it is scrambled in such a manner that makes it 
   _computationally infeasible_ to recover the original (unencrypted) data 
   without knowledge of the appropriate key.
3. A _symmetric key_ works much like a physical key for a door; the same key can 
   be used to both lock and unlock the door. If a symmetric key is used in a 
   cryptographic function to encrypt some data, the _same key_ must be used with 
   the _same cryptographic function_ to recover the original unencrypted data.
4. An _asymmetric key pair_ consists of a _private key_ and a _public key_. 
   These key pairs have the property that if the _public key_ is used in a 
   cryptographic function to encrypt some data, the corresponding _private key_
   must be used with the same cryptographic function to recover the original
   (unencrypted) data.
5. A password can be used to generate a symmetric key, and the resulting 
   symmetric key can be used to encrypt or decrypt data. A password is often
   used to encrypt the private key of an asymmetric key pair so that the 
   private key remains private.   

### What's a Credential?

We often informally use the term _certificate_ to mean a credential that is used 
to identify some entity; a person, a service or server, etc.  A credential is
actually a _named asymmetric key pair_. The public key portion of the key pair
is signed by a certification authority; the authority certifies that the key 
pair represents (or belongs to) a given entity; a person, a service or server, 
etc. In practice, a credential consists of a private key, a certificate which
identifies the subject entity (person, service, etc.) and contains the
corresponding public key, and a chain of certificates that validate the identity
of the certificate authority that issued our credential's certificate.

The private key portion of a credential must be protected, since anyone (or 
anything) that can demonstrate that she controls the private key is assumed to
be the entity represented by the credential. If Mallory has the private key for 
a credential representing Annie, Mallory can pretend to be Annie in any
context in which Annie's credential is accepted as a representation of Annie
herself.

### A Credential Storage Analogy

Credo is designed to allow credentials to be securely stored such that they
can be accessed by groups of individuals who are authorized to access them. It
is designed to prevent unauthorized access to stored credentials by anyone else.
It uses strong cryptographic techniques to achieve this end. In order to 
understand how and why this works, it's helpful to consider an analogy.

Suppose that the private key for our credential were just a simple document on
paper, like the title of ownership for an automobile. If that were the case, we 
could put the document inside of a lock box that is secured by another key. But 
then how do we secure the key for the lock box? We could put the key for our 
lock box in another lock box, with yet another key, but that's got to end 
somewhere in order to be useful. 

![Figure: Lock Box] (docs/images/lock-box.png)

What if we instead put the document for our credential into a safe with a 
combination lock? If we gave the combination to Annie, she could open the safe 
to retrieve the document. We could also give the combination to Brandon and 
Claire, and any one of them could open the safe to retrieve the document for
our credential.

![Figure: Combination Safe] (docs/images/combination-safe.png)

But what if Brandon leaves our company to go work for someone else? Then we'll
need to change the combination of the safe so that he no longer has access to
the credential document(s) stored in the safe. That's not too bad if there's 
only two other people who need to know the new combination, but it's much worse 
if there is more than one safe (each containing different documents) and 
different groups of people who know the combination of each safe. We need a 
better way.

Let's go back to the idea of putting our credential document into a simple lock 
box with a physical key. What if we made a copy of the lock box key and put it 
into a safe to which only Annie knows the combination? We could similarly make 
copies of the lock box key and lock them in separate safes for which only 
Brandon and Claire, respectively, know the corresponding combinations. 

![Figure: Lock Box and Combination Safes] (docs/images/lock-box-and-safe.png)

In order to get access to the credential document, Annie would use the 
combination she knows to open her safe, retrieve the key for the lock box, and 
then open the lock box to retrieve the document. If Brandon leaves the company, 
we simply need to ensure that he no longer has access to his safe in order to 
prevent him from accessing our credential document(s).

Note that the idea of a safe per person also nicely supports having multiple
lock boxes each containing a document for a different credential. In Claire's 
safe, we can put copies of the keys for all of the lock boxes containing 
documents she needs to access.

![Figure: Multiple Keys in One Safe] (docs/images/multiple-keys.png)

If we're clever enough, we could make it possible to put a lock box key into 
Claire's safe without needing to know the safe's combination. With physical keys 
a simple slot or drawer that drops the keys out of reach inside of the safe 
might suffice. The advantage of this is that now Claire is the _only_ person who 
needs to know the combination for her safe.

Another problem we need to consider is that if these safes are sitting out in 
the open somewhere, a determined and skilled person could spend his time trying 
to deduce the combination of the safe. If the lock is of high quality, this 
might be difficult, but with enough time and determination, any combination lock
can eventually be defeated by someone possessing the necessary skills.

To address this latter problem, we will probably want to install our safes and
(and lock boxes) in a secure structure so that they cannot be easily accessed 
by an unauthorized person. We would additionally want to put measures in place 
to carefully monitor those persons who access the structure containing these 
safes. These sorts of strategies are common in settings such as banks, 
government facilities, etc.

### Applying Cryptographic Techniques to Credential Storage

Credo uses cryptographic techniques to achieve the same outcomes described in
our analogy, without the need for physical keys and safes.

An asymmetric key pair whose private key is encrypted using a password can 
function like the combination safe in our analogy. An article can be put into the 
"safe" by encrypting it using the public key; this is analogous to dropping 
something into the safe by means of a drawer or slot (without actually opening 
the safe). A person who knows the password that was used to encrypt the private 
key can use the password to "open" the safe; the password is used to decrypt the 
private key, and the private key is then used to recover (decrypt) the original 
article(s). 

When Annie logs into Credo for the first time, Credo generates an asymmetric 
key pair exclusively for her use. Credo asks her to provide a password that will 
be used to encrypt her private key. Credo doesn't store her password; only 
Annie knows the password she selected. However, Credo does store a one-way
hash of Annie's password. It uses this hash to more easily validate a password
provided as input.

![Figure N: User Profile] (docs/images/user-profile.png)

Annie's asymmetric key pair plays the role of the combination safe in our 
analogy. Articles can be placed into her "safe" by encrypting them using her 
public key. Since Annie is the only person who knows her password, she is the 
only one who can open her "safe"; she can use her password to decrypt her 
private key, which can in turn decrypt articles that were encrypted using her 
public key.

In Credo, a group of people can share access to a credential. When the private 
key for a credential is stored, a symmetric key is used to encrypt the private 
key. This is analogous to putting the credential's private key into a lock box. 
Credo makes a copy of the symmetric key (the key for the "lock box") for each 
member of the access group, and then encrypts this key using the corresponding
member's public key. This effectively puts a copy of the key for the "lock box" 
(which contains the credential we're trying to protect) into each group member's 
"safe".

![Figure N: Credential Protection] (docs/images/credential-protection.png)

Suppose Claire is a member an access group assigned to a stored credential. She 
can use her password to decrypt her private key; she knows the combination to
her "safe". Her (decrypted) private key can then be used to decrypt her copy 
of the symmetric key that was used to encrypt the credential's private key; 
she can retrieve the "lock box key" from her "safe". The (decrypted) symmetric 
key can then be used to decrypt the private key for the stored credential; she 
can unlock the "lock box" to retrieve the credential's private key.

![Figure N: Credential Access] (docs/images/credential-access.png)


### Applying Groups to Securely Manage Access

The symmetric key used to encrypt the private key for a credential is associated
with the access group assigned to the credential. When a credential is to be 
stored in Credo, the user specifies which access group will be the _owner_ of 
the credential. 

When a group is created, the user who created the group is automatically added 
as a member. The user who creates the group can add other members. Credo 
generates a symmetric key for the group, but it never stores this key in 
unencrypted form. Instead it makes a copy of the group's symmetric key for each
group member, and encrypts each copy using the corresponding member's public key 
before storing it. 

In addition to specifying members for a group, the user who creates a group can
specify an existing group as the group's _owner_. The owner group is the parent
of the group. We'll discuss what this implies shortly, but for now it is 
important to know that a group can have a owner, but it isn't required.

![Figure N: Group with no Owner] (docs/images/group.png)

Credo allows any member of a group with no owner to add or remove members from
the group. In order to add members, Credo needs to be able to make copies of
the group's symmetric key and encrypt each copy with the public key of a
member to be added to the group. Since Credo does not store any unencrypted 
copies of the group's symmetric key, Credo prompts the user to enter the 
password for her private key. It then decrypts the user's private key, and 
uses it to decrypt the user's copy of the group's symmetric key. It can then 
make copies of the decrypted symmetric key and encrypt them using the public
keys of the members to be added.

Reading between the lines a bit, this design implies that it is impossible for 
Credo to add a member to an owner-less group without the cooperation of a user 
who is already a member of the group -- only group members can decrypt the
group's symmetric key. This also implies that an owner-less group must have at
least one member, since otherwise there would exist no copy of the group's
symmetric key.

### Group Hierarchy

The concept of a group owner allows groups to be organized in a hierarchy. This 
allows membership in a group to be inherited, which is a desirable feature in a 
group-based access control mechanism.

As previously discussed, a group's symmetric key can be recovered only by 
members of the group. Since Credo does not store an unencrypted copy of the
group's symmetric key, how can members of a parent (or other ancestor) group
gain access to the group's symmetric key in order to in turn gain access to
credentials owned by the group?

Credo solves this problem by making another copy of a group's symmetric key and
encrypting it using the symmetric key of the owner group.   

![Figure N: Group with Owner] (docs/images/group-hierarchy.png)

Suppose we have a credential that is owned by group `my-app-admins` whose
owner group is `my-root`.  If Annie is a member of `my-root`, Credo
can use her private key to decrypt the secret key for `my-root`. It can then 
use the secret key for `my-root` to decrypt the secret key for `my-app-admins`.
The secret key for `my-app-admins` can then be used to decrypt the key for the
stored credential.

![Figure N: Hierarchical Credential Access] (docs/images/hierarchical-access.png)

In the third step, Credo uses the secret key for an owner group to decrypt
the key for a group.  This step can be repeated as many times as necessary --
for any sequence of ancestor groups, starting with a member of an ancestor 
group, Credo can decrypt the secret keys of each successive descendant
group, all the way down to the group that owns a credential of interest.

The presence of a group owner also allows group membership to be managed and 
controlled by members of the group owner. As previously discussed, the members
of an owner-less group are allowed to manage the group's membership. When a 
group has an owner, Credo allows the group to be edited or deleted only by a 
member of the owner group (or one of its ancestors). 

When creating or editing a group, the user may assign the owner group.  Only 
a group in which the user is a member (directly or indirectly through the 
hierarchy) an be assigned as the owner of a group.

### User's Implicit `self` Group

When Credo creates a profile for a user, it automatically creates a group 
that contains the user as its one and only member.  This group has no owner
group, and its membership cannot be changed. This group is designated as `self`
in the application. Each user has her own `self` group.

A user can assign ownership of a credential to `self` in order to restrict 
access such that no other Credo user has access to the credential. This is
useful for personal credentials.


Wildfly Setup Notes
-------------------

* Users need to have the `org.soulwing.credo.user` role
* Put the PostgreSQL driver jar (`postgresql-9.4-1201-jdbc41.jar`) into 
  Wildfly's deployment directory 
* Create a PostgreSQL database for Credo.  It should be named `credo`.  A user
  named `credo` should be granted full privileges on the database. You can 
  assign any password to the database user. 
* Use the Wildfly CLI to create the datasource that Credo will use to access the
  database.  See the example CLI command below. 
  - specify the correct database password
  - if the PostgreSQL database doesn't run on the same host as the Wildfly
    container, specify the appropriate hostname in the JDBC URL
  - if your PostgreSQL cluster listens on a port other than the default, be sure
    to specify the appropriate port in the JDBC URL; it goes after the hostname, 
    delimited by a colon (:) character
  - if you used a different version of the JDBC driver, specify the appropriate
    driver name
    
```
/subsystem=datasources/data-source=credo:add(
  jndi-name=java:/jdbc/datasources/credo, use-ccm=false, 
  connection-url=jdbc:postgresql://localhost/credo,
  driver-name=postgresql-9.4-1201-jdbc41.jar, 
  user-name=credo, password=PASSWORD_HERE)
``` 
