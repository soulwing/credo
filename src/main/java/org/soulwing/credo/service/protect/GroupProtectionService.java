/*
 * File created on Mar 14, 2014 
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
package org.soulwing.credo.service.protect;

import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

/**
 * A service that provides encryption services for the secret key associated
 * with a {@link UserGroup}.
 *
 * @author Carl Harris
 */
public interface GroupProtectionService {

  /**
   * Protects the secret key for the given group using the public key
   * of the given user.
   * <p>
   * This method persists the given user as a member of the subject group
   * with an encrypted copy of the given secret key.
   * @param group the subject group 
   * @param secretKey secret key associated with {@link group}
   * @param profile user profile whose public key will encrypt the secret key
   */
  void protect(UserGroup group, SecretKeyWrapper secretKey,
      UserProfile profile);
  
  /**
   * Unprotects the secret key for the given group using the private key
   * of the currently logged in user.
   * @param group the subject group
   * @param password password provided by the logged in user
   * @return secret key
   */
  SecretKeyWrapper unprotect(UserGroup group, Password password) 
      throws GroupAccessException, UserAccessException;
  
}
