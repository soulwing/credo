/*
 * File created on Mar 21, 2014 
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

import javax.crypto.SecretKey;
import javax.inject.Inject;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.crypto.Encoded;
import org.soulwing.credo.service.crypto.Encoded.Type;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * An abstract base for private key protection services.
 *
 * @author Carl Harris
 */
public abstract class AbstractCredentialKeyProtectionService {

  @Inject
  protected UserGroupRepository groupRepository;
  
  @Inject
  protected UserContextService userContextService;
  
  @Inject
  protected GroupProtectionService groupProtectionService;
  
  @Inject
  protected PrivateKeyEncryptionService privateKeyEncryptionService;
  
  @Inject
  @Encoded(Type.AES)
  protected PrivateKeyDecoder aesDecoder;

  /**
   * Obtains the secret key associated with a group.
   * @param protection protection parameters which identify the group and
   *    provide the password obtained from the logged-in user
   * @return secret key
   * @throws NoSuchGroupException if the specified group does not exist
   * @throws GroupAccessException if the user is not a member of the 
   *    specified group
   * @throws UserAccessException if the user's private key cannot be
   *    accessed using the specified password
   */
  protected SecretKey getGroupSecretKey(ProtectionParameters protection)
      throws NoSuchGroupException, GroupAccessException, UserAccessException {
        UserGroup group = findGroup(protection.getGroupName());
        return groupProtectionService.unprotect(group, 
            protection.getPassword()).derive();
      }

  /**
   * Finds a group by name.
   * @param groupName name of the group to match
   * @return group object
   * @throws NoSuchGroupException if the group does not exist
   */
  protected UserGroup findGroup(String groupName) throws NoSuchGroupException {
    UserGroup group = groupRepository.findByGroupName(groupName, 
        userContextService.getLoginName());
    if (group == null) {
      throw new NoSuchGroupException();
    }
    return group;
  }

  /**
   * Wraps (encrypts) a private key using a secret key.
   * @param privateKey the private key to wrap
   * @param secretKey the secret key to use to encrypt {@code privateKey}
   * @return wrapped private key
   */
  protected PrivateKeyWrapper wrapPrivateKey(PrivateKeyWrapper privateKey, 
      SecretKey secretKey) {
    return privateKeyEncryptionService.encrypt(privateKey, secretKey);
  }

  /**
   * Unwraps (decrypts) a private key using a secret key.
   * @param privateKey the private key to unwrap
   * @param secretKey the secret key to use to decrypt {@code privateKey}
   * @return private key
   */
  protected PrivateKeyWrapper unwrapPrivateKey(String encodedPrivateKey, 
      SecretKey secretKey) {
  
    PrivateKeyWrapper encryptedCredentialKey = aesDecoder.decode(
        encodedPrivateKey);
    
    encryptedCredentialKey.setProtectionParameter(
        secretKey);
    
    return encryptedCredentialKey.deriveWrapper();
  }

}
