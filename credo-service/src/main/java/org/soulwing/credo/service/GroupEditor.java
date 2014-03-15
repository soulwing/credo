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


/**
 * An editor for a group.
 *
 * @author Carl Harris
 */
public interface GroupEditor {

  /**
   * Gets the group's unique identifier.
   * @return unique identifier or {@code null} if the group in the editor
   *    is transient
   */
  Long getId();
  
  /**
   * Gets the group name.
   * @return group name
   */
  String getName();
  
  /**
   * Sets the group name.
   * @param name the name to set
   */
  void setName(String name);
  
  /**
   * Gets the group description.
   * @return group description
   */
  String getDescription();
  
  /**
   * Sets the group description.
   * @param description the description to set
   */
  void setDescription(String description);
  
  /**
   * Gets the membership of the group.
   * @return array of member user IDs
   */
  Long[] getMembership();
  
  /**
   * Sets the membership of the group.
   * <p>
   * This method replaces the group's existing members with the members in 
   * the given set.
   * @param membership the set of member user IDs
   */ 
  void setMembership(Long[] membership);
  
  /**
   * Gets the collection of all users.
   * @return collection of users
   */
  Collection<UserDetail> getAvailableUsers();

}
