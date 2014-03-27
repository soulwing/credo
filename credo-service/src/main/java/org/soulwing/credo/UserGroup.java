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
package org.soulwing.credo;

/**
 * A group of users who share ownership of assigned credentials.
 *
 * @author Carl Harris
 */
public interface UserGroup extends Owned {

  /**
   * Name of the "self" group.
   */
  String SELF_GROUP_NAME = "self";
  
  /**
   * Gets the unique identifier of the group.
   * @return group's unique identifier
   */
  Long getId();
  
  /**
   * Gets the name of the group.
   * @return group name
   */
  String getName();
 
  /**
   * Sets the name of the group.
   * @param name the group name to set
   * @param name
   */
  void setName(String name);
  
  /**
   * Gets the group description.
   * @return description
   */
  String getDescription();
  
  /**
   * Sets the group description.
   * @param description the description to set
   */
  void setDescription(String description);

  /**
   * Gets the group that owns this group.
   * @return group or {@code null} if this group doesn't have an owner
   */
  UserGroup getOwner();
  
  /**
   * Sets the group that owns this group.
   * @param owner the owner to set
   */
  void setOwner(UserGroup owner);
  
  /**
   * Gets the group owner's copy of the group secret key.
   * @return secret key or {@code null} if this group does not have an owner
   */
  String getSecretKey();
  
  /**
   * Sets the group owner's copy of the group secret key.
   * @param secretKey the secret key to set
   */
  void setSecretKey(String secretKey);

}

