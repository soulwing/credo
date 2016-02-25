/*
 * File created on Apr 14, 2014 
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
package org.soulwing.credo.service.group;

import javax.ejb.Local;

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;

/**
 * A service that supports removing a group via a UI interaction.
 *
 * @author Carl Harris
 */
@Local
public interface RemoveGroupService {

  /**
   * Finds a group using its unique identifier.
   * @param id unique identifier of the group to match
   * @return group detail
   * @throws NoSuchGroupException if the specified group does not exist
   */
  GroupDetail findGroup(Long id) throws NoSuchGroupException;
  
  /**
   * Removes the group with the given unique identifier.
   * <p>
   * This method does not throw an exception if the group to remove no
   * longer exists.
   * 
   * @param id unique identifier of the group to remove
   * @param errors errors object that will be updated if the group cannot be
   *    removed
   * @throws GroupException if the group cannot be removed (e.g. because it
   *    is in use)
   * @throws GroupAccessException if the logged in user is not authorized
   *    to remove the group
   */
  void removeGroup(Long id, Errors errors) 
      throws GroupException, GroupAccessException;

}
