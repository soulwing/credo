/*
 * File created on Mar 4, 2014 
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
package org.soulwing.credo.facelets;

import org.soulwing.credo.service.ProtectionParameters;

/**
 * A {@link ProtectionParameters} object modeled as a simple bean.
 *
 * @author Carl Harris
 */
public class ImportProtectionParameters implements ProtectionParameters {

  private String groupName;
  private String loginName;
  private char[] password;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getGroupName() {
    return groupName;
  }

  /**
   * Sets the group name.
   * @param groupName the group name to set.
   */
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getLoginName() {
    return loginName;
  }

  /**
   * Sets the login name.
   * @param loginName the login name to set
   */
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public char[] getPassword() {
    return password;
  }

  /**
   * Sets the password.
   * <p>
   * The password is cloned before being stored.
   * @param password the password to set
   */
  public void setPassword(char[] password) {
    this.password = password;
  }
  
  /**
   * Clears the stored password.
   */
  public void clearPassword() {
    for (int i = 0; i < password.length; i++) {
      password[i] = 0;
    }
  }
  
}
