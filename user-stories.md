Signing Request for Existing Credential
---------------------------------------

A user can create a signing request to renew an existing credential.

* The subject name for the request is the same as the subject name on the
  credential.
* The friendly name, owner, and description, and tags should be copied from 
  the existing credential, and the user should have the opportunity to edit 
  them before saving the request.


Signing Request for New Credential
----------------------------------

A user can create a signing request for a new credential.

* A subject common name must be specified; additional X.509 name components
  may be specified
* A friendly name must be specified; the default value is the subject common
  name
* An owner must be specified
* A description and zero or more tags may be specified


View Signing Requests
---------------------

A user can view a table of signing requests for which she is an owner.

* It should be a table view with the friendly name, issuer name, date 
  created, and a flag that indicates whether the request has been completed
  (i.e. whether it has been used to create a credential).
* Each row in the table should include controls to edit or remove the 
  request, and to import the signed certificate chain.
 
 
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


Edit Credential Signing Request
-------------------------------

A user can edit an existing signing request for which she is an owner.

* The friendly name, description, and tags, and owner can be changed.
* None of the attributes of the X.509 signing request object can be changed.


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


Remove Credential Signing Request
---------------------------------

A user can remove a signing request for which she is an owner.

* The user should be prompted to confirm that she wishes to remove the
  signing request.
* If the signing request is not completed, there should be an additional 
  warning that the private key for the request cannot be recovered.
  

Credential Expiration Report
----------------------------

A user can view a report of credentials that are expiring in the near future.

* The report includes only those credentials for which the user is an owner.
* She can choose an interval of 15 days, 30 days, 60 days, or 90 days.
* The report includes the subject/friendly name, issuer, expiration date.
* The report has a printable version.


  
 