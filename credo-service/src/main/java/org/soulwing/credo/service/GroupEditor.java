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

import java.io.Serializable;
import java.util.Set;

/**
 * An editor for a group.
 *
 * @author Carl Harris
 */
public interface GroupEditor extends GroupDetail {

  /**
   * Sets the group name.
   * @param name the name to set
   */
  void setName(String name);
  
  /**
   * Sets the group description.
   * @param description the description to set
   */
  void setDescription(String description);
  
  /**
   * Sets the membership of the group.
   * <p>
   * This method replaces the group's existing members with the members in 
   * the given set.
   * @param userIds the set of member user IDs
   */ 
  void setMembers(Set<Serializable> userIds);
  
}