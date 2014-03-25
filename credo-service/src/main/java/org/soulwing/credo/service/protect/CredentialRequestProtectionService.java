/*
 * File created on Mar 20, 2014 
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

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * A service that provides encryption services for the private key associated
 * with a {@link CredentialRequest}.
 *
 * @author Carl Harris
 */
public interface CredentialRequestProtectionService {

  /**
   * Applies cryptographic protection to a request's private key. 
   * @param request the subject request
   * @param privateKey private key associated with the request
   * @param protection protection parameters
   * @throws GroupAccessException if the logged-in user is not a member 
   *    of the group specified in the protection parameters
   * @throws UserAccessException if the user's profile cannot be accessed
   *    with the password specified in the protection parameters
   * @throws NoSuchGroupException if the group specified in the protection
   *    parameters does not exist
   */
  void protect(CredentialRequest request, PrivateKeyWrapper privateKey,
      ProtectionParameters protection) throws GroupAccessException, 
      UserAccessException, NoSuchGroupException;
  
  /**
   * Removes the cryptographic protection from a credential request's private 
   * key.
   * @param request the subject request
   * @param protection protection parameters
   * @return unprotected private key associated with {@code request}
   * @throws GroupAccessException if the logged-in user is not a member
   *    of the group specified in the protection parameters
   * @throws UserAccessException if the user's profile cannot be accessed with
   *    the password specified in the protection parameters   *    
   */
  PrivateKeyWrapper unprotect(CredentialRequest request, 
      ProtectionParameters protection) throws UserAccessException,
      GroupAccessException;
  

}
