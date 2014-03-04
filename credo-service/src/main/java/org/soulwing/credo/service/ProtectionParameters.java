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
package org.soulwing.credo.service;


/**
 * An object that provides the parameters that will be used to protect
 * the private key for a credential. 
 *
 * @author Carl Harris
 */
public interface ProtectionParameters {

  /**
   * Gets the group name.
   * @return group name
   */
  String getGroupName();
  
  /**
   * Gets the user's login name.
   * @return login name
   */
  String getLoginName();

  /**
   * Gets the user's password.
   * @return password
   */
  char[] getPassword();

}
