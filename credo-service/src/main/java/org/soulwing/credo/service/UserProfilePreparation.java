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

import org.soulwing.credo.Password;

/**
 * An object that is prepared in anticipation of creating a new user profile.
 *
 * @author Carl Harris
 */
public interface UserProfilePreparation {

  /**
   * Gets the login name associated with the new profile.
   * @return login name
   */
  String getLoginName();
  
  /**
   * Gets the full name associated with the new profile.
   * @return full name or {@code null} if no full name has been assigned
   */
  String getFullName();
  
  /**
   * Sets the full name associated with the new profile.
   * @param fullName the full name to set
   */
  void setFullName(String fullName);
  
  /**
   * Gets the password associated with the new profile.
   * @return password of {@code null} if no password has been set
   */
  Password getPassword();
  
  /**
   * Sets the password associated with the new profile.
   * @param password the password to set
   */
  void setPassword(Password password);
  
}
