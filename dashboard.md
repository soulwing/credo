The main view of the application is a dashboard that provides some useful
views:

* Credentials that are expiring soon
* Activity stream -- shows what users have done recently


The view for credentials that are expiring soon shows the name of the 
credential and expiration date.  It includes a single control that will
initiate the next action needed to prevent the credential from expiring:

*  If the credential has no associated request to renew, a Renew button is 
   displayed that when clicked will initiate the interaction to create a 
   request to renew the credential.  
*  If the credential has an associated renewal request, an Import button is 
   displayed that when clicked will initiate the interaction to import the 
   signed certificate.
   
The activity stream shows the recent activity in the application that is
relevant to the logged in user.  This includes activity by the user herself,
along with the activities of other users who are members of the groups for 
which she is a member.

The types of activities that are relevant to a user include:

*  User added to or removed from a group for which she is also a member.
*  The ownership of a credential or request is changed
*  Credential owned by one of her groups is imported, edited, deleted, 
   exported.
*  Credential request is created or edited for a credential or request owned 
   by one of her groups.
*  Credential is created from a pending request when one of her groups is
   an owner of either the request or the credential.
*  Credential request owned by one of her groups is deleted without creating
   a credential.
*  User who is a member of one of her groups has logged in or has logged out


The attributes of an action included in the activity view include:

*  The avatar and name of user who performed the action
*  When the action occurred.  For recent actions use expressions such as 
   "just now", "today at 8:57 AM", "yesterday at 2:34 PM", "Apr 4".
   For actions that occurred a month or more in the past, use just the date;
   "Feb 14 2014"
*  A description of the action with links to the subject entity

For example:

*  [{IMG} Laurie Zirkle] imported [desktop.alerts.vt.edu]
   just now
*  [{IMG} Carl Harris] changed the owner of [CNS-Tact] from developers 
   to tomcat-admins
   yesterday at 5:42 AM
*  [{IMG} Jacob Dawson] renewed [VT-Wireless.cns.vt.edu]
   Apr 4
*  [{IMG} Laurie Zirkle] added [Jacob Dawson] to freeradius-admins
   Mar 29   
   

The details of the recorded action need to remain useful even at a later 
point in time when one or more of the subject entities might have been
removed.

*  Need to know the identity of the person who performed the action even
   if the user profile is later removed.
*  Need to know the names of the affected credential(s), request(s), or 
   group(s) even when those entities no longer exist.
   

An action has a templated textual description and an array of links to
details.  The textual description uses numbered placeholders in which the
linked text will be inserted.  For example

   "changed the owner of {1} from {2} to {3}"
   
There is an implicit placeholder for a linked detail with index 0 at the 
beginning of each templated textual description, and the linked detail is 
always the user who performed the action.

The linked detail can produce either simple text or an actual HTML hyperlink,
and the decision as to which to produce can be deferred until the template is
resolved for display.  This allows a linked detail to produce a hyperlink for 
an entity such as a credential for as long as the credential exists, but to 
revert to producing just the name of the credential if the credential is 
subsequently removed.

A linked detail has an optional image attribute.  The image is used only for
the detail describing the person, and is a 48x48 pixel avatar representing the
person.






   
   
    


