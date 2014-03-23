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


Edit Credential
---------------

A user can edit an existing credential for which she is an owner.

* The name, note, and tags can be edited.
* The owner group can be changed to another group of which she is a member.


Edit Credential Signing Request
-------------------------------

A user can edit an existing signing request for which she is an owner.

* The friendly name, description, and tags, and owner can be changed.
* None of the attributes of the X.509 signing request object can be changed.


Signing Request for New Credential
----------------------------------

A user can create a signing request for a new credential.

* A subject common name must be specified; additional X.509 name components
  may be specified
* A friendly name must be specified; the default value is the subject common
  name
* An owner must be specified
* A description and zero or more tags may be specified


Extensions for Signing Request
------------------------------

When creating a signing request a user can specify X.509 extensions to 
include in the request

* Constraints that can be specified must include
    * Basic Constraint (is the request for a CA certificate?)
    * Key usage (support all of the standard usages)
    * Extended key usage (at least for common TLS Web Server/Client usages)
    * Subject alternative names (at least the DNS and e-mail name types)
* When creating a signing request to renew an existing credential the
  default values for all supported extensions should come from the existing
  certificate


Credential Expiration Report
----------------------------

A user can view a report of credentials that are expiring in the near future.

* The report includes only those credentials for which the user is an owner.
* She can choose an interval of 15 days, 30 days, 60 days, or 90 days.
* The report includes the subject/friendly name, issuer, expiration date.
* The report has a printable version.


  
 