
Presently, a group consists of a name, description, and a set of users who
are designated as members.  Each member of a group retains an encrypted copy
of the group's secret key.  This secret key is used in managing the private 
keys of credentials owned by the group.

Any group member can change the membership of the group or modify other
properties of the group.  In some settings, this might not be acceptable.
For this reason, we want to introduce the concept of a group owner.

The owner for a group is itself a group.  Each member of an owner group is
allowed to change the membership and properties of each group it owns.  A
member of a group can use the group (and in particular, can decrypt the
group's secret key), but cannot change the membership of the group.

A group may have no owner, in which case the group is effectively owned by 
itself, in the sense that any member an change the membership of the group or 
modify other properties of the group.

Any group which an owner other than itself retains a copy of its own secret 
key, encrypted using the secret key of its owner.  Thus for a group G with an
owner other than G and with n members, there are n + 1 copies of the secret
key for G; one for each member, encrypted using the public key
of the member, and one for the owner, encrypted using the secret key of the 
owner.  If a group G* is any ancestor of G, then each member of G* is 
effectively a member of G in the sense that the member's private key can 
decrypt G*'s secret key, which implies that the member can decrypt the secret
key of every group between G* and G.


One important operation is identifying whether a given group G is a descendant
of another group G', using an SQL database query.  SQL does not provide
intrinsic support for such hierarchies in a vendor-independent manner.  In
this application, group hierarchies are not likely to be very deeply
nested.  The most common situations will be no more than two or three levels
deep.  This suggests a strategy where the row representing a group has a 
column that stores a representation of the group's ancestry as a simple path
string.  

For example, suppose that group 10 is a child of group 8.  The contents of
the path column for group G would contain the identifier for G':

ID  PATH
--  -----------------------------------------------------
10  /8


An example of some more complex relationships:

ID  PATH
--  -----------------------------------------------------
20  /8/10/11/
15  /4/12/
12  /4/
11  /8/10/
10  /8/
8   /
4   /


When assigning an owner G' to a group G, the identifier for G' is appended 
to the path for G' along with a trailing slash character (/), and the result
is assigned as the path for G.  For a group G with no owner, a single slash
character is assigned as the path.

With this construction, is it easy to discern with a given group G' appears
in the ancestry for a group G, by a query such as this:

select g from UserGroupEntity g
where g.path like :pattern

where the query parameter "pattern" is assigned a string value of the form:

%/{ID}/%

Where {ID} is the unique identifier of G'


Another important operation is to determine whether a given user is a member
of group G or any of its ancestors.  The ancestry path provides the unique
identifiers of the set of ancestors for G.  The set of groups to consider
can be easily retrieved in a single query using the IN operator:

select g from UserGroupEntity g
where g.id = :id or g.id in :ancestors

where the query parameter "ancestors" is derived from the group's ancestry 
path:

String path = g.getAncestryPath();
String[] ancestors = path.substring(1, path.length() - 1).split("/");

Using a join to the UserGroupMemberEntity we can easily locate the member 
record for a user who is a member of G or any of its ancestors:

select distinct gm from UserGroupMemberEntity gm
inner join gm.group g
inner join gm.user u
where g.id = :id or g.id in :ancestors
and u.loginName = :loginName






