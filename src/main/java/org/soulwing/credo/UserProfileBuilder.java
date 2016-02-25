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
 * A builder that constructs {@link UserProfile} objects.
 *
 * @author Carl Harris
 */
public interface UserProfileBuilder {

  /**
   * Sets the receiver's login name.
   * @param loginName the login name to set
   * @return the receiver
   */
  UserProfileBuilder setLoginName(String loginName);
  
  /**
   * Sets the receiver's full name.
   * @param fullName the full name to set
   * @return the receiver
   */
  UserProfileBuilder setFullName(String fullName);

  /**
   * Sets the receiver's encrypted password.
   * @param password the encrypted password to set
   * @return the receiver
   */
  UserProfileBuilder setPassword(String password);
  
  /**
   * Sets the receiver's public key.
   * @param publicKey PEM encoded public key
   * @return the receiver
   */
  UserProfileBuilder setPublicKey(String publicKey); 
  
  /**
   * Sets the receiver's private key.
   * @param privateKey PEM encoded private key
   * @return the receiver
   */
  UserProfileBuilder setPrivateKey(String privateKey);

  /**
   * Builds a profile according to the receiver's configuration.
   * @return profile object
   * @throws IllegalStateException if the configuration is incomplete or
   *    inconsistent
   */  
  UserProfile build();
  
}
