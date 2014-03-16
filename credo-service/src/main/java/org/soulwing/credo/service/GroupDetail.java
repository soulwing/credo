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
 * An object that describes the details of a group.
 *
 * @author Carl Harris
 */
public interface GroupDetail {

  /**
   * Gets the group's unique identifier.
   * @return unique identifier
   */
  Long getId();
  
  /**
   * Gets the group's name.
   * @return group name
   */
  String getName();
  
  /**
   * Gets the group's description.
   * @return group description
   */
  String getDescription();
  
  /**
   * Gets the users that are members of this group.
   * @return collection of members
   */
  Collection<UserDetail> getMembers();
  
  /**
   * Tests whether the group is in use for any purpose (e.g. as the owner 
   * of a credential).
   * @return {@code true} if the group is in use
   */
  boolean isInUse();
  
    
}
