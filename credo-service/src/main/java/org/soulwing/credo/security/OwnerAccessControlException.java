/*
 * File created on Mar 22, 2014 
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
package org.soulwing.credo.security;

import org.soulwing.credo.Owned;

/**
 * An exception thrown on an attempt to access an {@link Owned} object by 
 * a logged in user who is not a member of the owner group. 
 *
 * @author Carl Harris
 */
public class OwnerAccessControlException extends RuntimeException {

  private static final long serialVersionUID = -369259065653910787L;

  private final String groupName;
  private final String loginName;
  
  /**
   * Constructs a new instance.
   * @param message
   */
  public OwnerAccessControlException(String groupName, String loginName) {
    super(loginName + " is not a member of " + groupName);
    this.groupName = groupName;
    this.loginName = loginName;
  }

  /**
   * Gets the {@code groupName} property.
   * @return
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * Gets the {@code loginName} property.
   * @return
   */
  public String getLoginName() {
    return loginName;
  }

}
