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
package org.soulwing.credo;

/**
 * An object that describes an application user.
 *
 * @author Carl Harris
 */
public interface UserProfile {

  /**
   * Gets the unique identifier for the user.
   * @return unique identifier (or {@code null} if the receiver is transient)
   */
  Long getId();
  
  /**
   * Gets the login name for the user.
   * @return login name
   */
  String getLoginName();
  
  /**
   * Gets the full name for the user.
   * @return full name
   */
  String getFullName();
  
  /**
   * Gets the user's encrypted password.
   * @return encrypted password
   */
  String getPassword();
  
  /**
   * Gets the user's PEM encoded public key.
   * @return public key
   */
  String getPublicKey();
  
  /**
   * Gets the user's encrypted, PEM encoded private key.
   * @return public key
   */
  String getPrivateKey();
  
}
