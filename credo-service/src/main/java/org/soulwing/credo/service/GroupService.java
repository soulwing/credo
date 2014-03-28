/*
 * File created on Mar 10, 2014 
 *
 * Copyright (c) 2014 Virginia Polytechnic Institute and State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.credo.service;

import java.util.Collection;

import javax.ejb.Local;

/**
 * A service that provides for access and manipulation of groups.
 *
 * @author Carl Harris
 */
@Local
public interface GroupService {

  /**
   * Creates a new group.
   * @return editor for the new group
   */
  GroupEditor newGroup();
  
  /**
   * Finds a group using its unique identifier.
   * @param id unique identifier of the group to match
   * @return group detail
   * @throws NoSuchGroupException if the specified group does not exist
   */
  GroupDetail findGroup(Long id) throws NoSuchGroupException;
  
  /**
   * Finds the collection of all groups that are accessible to the logged in
   * user.
   * @return collection of groups
   */
  Collection<GroupDetail> findAllGroups();
  
  /**
   * Creates an editor for the given group.
   * @param id unique identifier for the group
   * @return editor instance
   * @throws NoSuchGroupException if the specified group does not exist
   *    (or is not visible to logged in user)
   */
  GroupEditor editGroup(Long id) throws NoSuchGroupException;
  
  /**
   * Applies the changes in the given editor to the group it represents, 
   * effectively making the requested changes persistent.
   * @param editor the editor to apply
   * @param errors an errors object that will be updated if the edits cannot
   *    be successfully applied
   * @throws GroupEditException if a recoverable error occurs in applying
   *    the editor to the target group
   * @throws NoSuchGroupException if an existing was removed after the
   *    editor was created
   * @throws PassphraseException if a password is required and was not
   *    provided or was incorrect
   * @throws GroupAccessException if the logged in user is not a member
   *    of the edited group
   * @throws MergeConflictException if the persistent state of the group
   *    has changed since the editor was created
   */
  void saveGroup(GroupEditor editor, Errors errors) 
      throws GroupEditException, NoSuchGroupException, GroupAccessException,
      PassphraseException, MergeConflictException;
  
  /**
   * Removes the group with the given unique identifier.
   * @param id unique identifier of the group to remove
   * @param errors errors object that will be updated if the group cannot be
   *    removed
   * @throws GroupEditException if the group cannot be removed (i.e because it
   *    is in use)
   * @throws NoSuchGroupException if the specified group does not exist
   */
  void removeGroup(Long id, Errors errors) 
      throws GroupEditException, NoSuchGroupException;
  
  /**
   * Tests whether the existing group name exists for the logged-in user.
   * @param groupName the subject group name
   * @return {@code true} if group name exists
   * @throws GroupAccessException if the logged-in user is not a member of
   *    the given group name
   */
  boolean isExistingGroup(String groupName) throws GroupAccessException;
  
}
