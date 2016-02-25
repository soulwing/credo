
Bugs
----

- Attempting to delete a credential that has an associated credential request 
  fails due to a database integrity constraint violation
- Specifying an owner group in import credential doesn't work (resulting group
  has owner "self"); as a workaround, can set group in Edit Credential screen
- The subject name should be a required field in the Create Credential Request 
  interaction
- The link for manually downloading a exported credential doesn't work 
  (no navigation happens)
- The Reports and About links don't work (404 Not Found)

Improvements
------------
- If the session times out the user gets an annoying message that says that 
  the system cannot restore the session.  Need a better message and a link to
  go back to home screen
- Every wizard interaction should include a Go Back control in each form
  - import credential
  - create request
  - import signed certificate
  - export credential (password form should include Go Back)
- The Create Credential Request details form should display the full subject
  name that will appear on the signing request
- The welcome page should tell the user why a password is needed (and how it
  will be used).
- The screens for editing the details of a certificate/request should provide
  a means of displaying the full X.500 subject name and issuer name.
- The user should be able to specify the key length for a credential request, 
  as well as subject alternative names, key usage and extended key usage, other
  supported extensions.

 

