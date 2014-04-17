/*
 * File created on Mar 5, 2014 
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

import javax.ejb.ApplicationException;

/**
 * An exception thrown when access to a group is not allowed (e.g. because
 * the logged in user is not a member of the group).
 *
 * @author Carl Harris
 */
@ApplicationException(rollback = true)
public class GroupAccessException extends Exception {

  private static final long serialVersionUID = -6443606842556968462L;

  private final String groupName;
  
  /**
   * Constructs a new instance.
   * @param groupName
   */
  public GroupAccessException(String groupName) {
    super("group " + groupName + " access denied");
    this.groupName = groupName;
  }

  /**
   * Gets the {@code groupName} property.
   * @return
   */
  public String getGroupName() {
    return groupName;
  }
  
}
