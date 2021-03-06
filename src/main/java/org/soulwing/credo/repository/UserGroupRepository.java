/*
 * File created on Mar 3, 2014 
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
package org.soulwing.credo.repository;

import java.util.List;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupFactory;

/**
 * A repository of persistent {@link UserGroup} objects.
 *
 * @author Carl Harris
 */
public interface UserGroupRepository extends UserGroupFactory {

  /**
   * Adds a group to the repository.
   * @param group the group to add
   */
  void add(UserGroup group);
  
  /**
   * Updates the given group, merging the state of the given (perhaps detached)
   * instance with the corresponding persistent instance.
   * @param group the group to update
   * @return the resulting persistent group
   */
  UserGroup update(UserGroup group);
  
  /**
   * Removes a group.
   * @param group the group to remove
   */
  void remove(UserGroup group);
  
  /**
   * Finds a group by its unique identifier.
   * @param id the unique identifier to match
   * @return group or {@code null} if no such group exists
   */
  UserGroup findById(Long id);
  
  /**
   * Finds a group by name.
   * @param groupName the group name to match or {@code null} to match the
   *   {@link UserGroup#SELF_GROUP_NAME}
   * @param loginName the login name of the user (ignored unless the "self"
   *    group is specified)
   * @return matching group or {@code null} if no such group can be found
   */
  UserGroup findByGroupName(String groupName, String loginName);

  /**
   * Finds the groups for which the given user is a member.
   * @param loginName login name of the subject user
   * @return set of groups for which the user with {@code loginName} is
   *     a member
   */
  List<UserGroup> findByLoginName(String loginName);

  /**
   * Finds the groups are owned by the given owner.
   * @param owner the group owner to match
   * @return set of groups owned by {@code owner}
   */
  List<UserGroup> findByOwner(UserGroup owner);
  
  /**
   * Finds all of the descendants of the given group.
   * @param group the subject group
   * @return set of descendant of {@code group}
   */
  List<UserGroup> findDescendants(UserGroup group);

}
