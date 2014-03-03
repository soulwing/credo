/*
 * File created on Mar 2, 2014 
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


/**
 * An object that represents a request to create a user profile.
 *
 * @author Carl Harris
 */
public class ConcreteUserProfilePreparation 
    implements UserProfilePreparation {

  private final String loginName;
  private String fullName;
  private char[] password;
    
  /**
   * Constructs a new instance.
   * @param loginName login name for the new profile
   */
  public ConcreteUserProfilePreparation(String loginName) {
    this.loginName = loginName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLoginName() {
    return loginName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getFullName() {
    return fullName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public char[] getPassword() {
    return password;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPassword(char[] password) {
    this.password = password;
  }
  
}
