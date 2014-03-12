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
  
  
Edit Group
----------

A user can edit an existing group for which she is the owner.

* The user can change the name and/or the description for the group, and 
  can add and remove group members as desired (except that she cannot remove
  herself from the group).  


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


Logged In User
--------------

A user sees her full name as the logged-in user in the top navigation bar.
 
  