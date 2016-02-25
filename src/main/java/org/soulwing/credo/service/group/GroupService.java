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
package org.soulwing.credo.service.group;

import java.util.Collection;

import javax.ejb.Local;

import org.soulwing.credo.service.GroupAccessException;

/**
 * A service that provides for access and manipulation of groups.
 *
 * @author Carl Harris
 */
@Local
public interface GroupService {

  /**
   * Finds the collection of all groups that are accessible to the logged in
   * user.
   * @return collection of groups
   */
  Collection<GroupDetail> findAllGroups();
  
  /**
   * Tests whether the existing group name exists for the logged-in user.
   * @param groupName the subject group name
   * @return {@code true} if group name exists
   * @throws GroupAccessException if the logged-in user is not a member of
   *    the given group name
   */
  boolean isExistingGroup(String groupName) throws GroupAccessException;
  
}
