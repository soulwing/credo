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


Subject Name for New Credential Request
---------------------------------------

A user can specify X.509 name components for the subject name when creating
a signing request for a new credential.


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


  
 