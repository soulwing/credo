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
package org.soulwing.credo.service.protect;

import org.soulwing.credo.Credential;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * DESCRIBE THE TYPE HERE.
 *
 * @author Carl Harris
 */
public interface CredentialProtectionService {

  /**
   * Applies cryptographic protection to a credential's private key.
   * @param credential the target credential
   * @param privateKey private key to protect and assign to {@code credential}
   * @param protection protect parameters
   * @throws UserAccessException if the user profile cannot be accessed
   * @throws GroupAccessException if the credential's group cannot be accessed
   */
  void protect(Credential credential, PrivateKeyWrapper privateKey, 
      ProtectionParameters protection)
      throws UserAccessException, GroupAccessException;
  
  /**
   * Removes the cryptographic protection from a credential's private key.
   * @param credential the subject credential
   * @param protection protection parameters
   * @return unprotected private key associated with {@code credential}
   * @throws UserAccessException
   * @throws GroupAccessException
   */
  PrivateKeyWrapper unprotect(Credential credential, 
      ProtectionParameters protection) throws UserAccessException,
      GroupAccessException;
  
}
