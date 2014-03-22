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

import java.util.Collection;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;

/**
 * A repository of persistent {@link UserGroupMember} objects.
 *
 * @author Carl Harris
 */
public interface UserGroupMemberRepository {

  /**
   * Adds a group member to the repository.
   * @param groupMember the group member to add
   */
  void add(UserGroupMember groupMember);
  
  /**
   * Removes a group member from the repository.
   * @param groupMember the group member to remove
   */
  void remove(UserGroupMember groupMember);
  
  /**
   * Removes a group member from the repository.
   * @param id unique identifier of the group to remove
   * @return {@code true} if a group member was removed
   */
  boolean remove(Long id);
  
  /**
   * Finds a group member by the associated user's unique ID.
   * @param groupName the subject group name
   * @param profile the user's profile identifier
   * @return group member or {@code null} if no such member exists
   */
  UserGroupMember findByGroupAndProfileId(String groupName, Long profileId);
  
  /**
   * Finds a group member for the given group and login name.
   * @param groupName the subject group name
   * @param loginName the subject user's login name
   * @return matching group member or {@code null} if no such member exists
   */
  UserGroupMember findByGroupNameAndLoginName(String groupName, String loginName);
  
  UserGroupMember findByGroupAndLoginName(UserGroup group, String loginName);
  
  /**
   * Finds the set of all members of a given group.
   * @param groupName group name (which must be neither {@code null} nor
   *    equal to {@link UserGroup#SELF_GROUP_NAME}.
   * @return set of group members
   */
  Collection<UserGroupMember> findAllMembers(String groupName);
  
  /**
   * Finds the groups for which the given user is a member.
   * @param loginName login name of the subject user
   * @return set of groups for which the user with {@code loginName} is
   *     a member
   */
  Collection<UserGroupMember> findByLoginName(String loginName);

  /**
   * Finds all group memebers for the given group and login name.
   * @param groupName unique identifier of the subject group
   * @param loginName the subject user's login name
   * @return matching group member or {@code null} if no such member exists
   */
  Collection<UserGroupMember> findByGroupIdAndLoginName(
      Long groupId, String loginName);

}
