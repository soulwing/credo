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
  
//  /**
//   * Finds the collection of all groups that are accessible to the logged in
//   * user.
//   * @return collection of groups
//   */
//  Collection<GroupDetail> findAllGroups();
//  
//  /**
//   * Creates an editor for the given group.
//   * @param id unique identifier for the group
//   * @return editor instance
//   * @throws NoSuchGroupException if the specified group does not exist
//   *    (or is not visible to logged in user)
//   */
//  GroupEditor editGroup(Long id) throws NoSuchGroupException;
  
  /**
   * Applies the changes in the given editor to the group it represents, 
   * effectively making the requested changes persistent.
   * @param editor the editor to apply
   * @param errors an errors object that will be updated if the edits cannot
   *    be successfully applied
   * @throws NoSuchGroupException if an existing was removed after the
   *    editor was created
   * @throws GroupEditException if a recoverable error occurs in applying
   *    the editor to the target group
   */
  void saveGroup(GroupEditor editor, Errors errors) 
      throws NoSuchGroupException, GroupEditException;
  
}
