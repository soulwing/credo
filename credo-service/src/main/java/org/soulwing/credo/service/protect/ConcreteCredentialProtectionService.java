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

import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.Credential;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.crypto.Encoded;
import org.soulwing.credo.service.crypto.Encoded.Type;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * A concrete {@link CredentialProtectionService} implementation.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteCredentialProtectionService
    implements CredentialProtectionService {

  @Inject
  protected UserGroupRepository groupRepository; 
  
  @Inject
  protected UserContextService userContextService;
  
  @Inject
  protected GroupProtectionService groupProtectionService;
  
  @Inject
  protected PrivateKeyEncryptionService privateKeyEncryptionService;
  
  @Inject @Encoded(Type.AES)
  protected PrivateKeyDecoder aesDecoder;

  /**
   * {@inheritDoc}
   */
  @Override
  public void protect(Credential credential, PrivateKeyWrapper privateKey,
      ProtectionParameters protection) throws UserAccessException,
      GroupAccessException, NoSuchGroupException {
    
    UserGroup group = findGroup(protection.getGroupName());
    SecretKey secretKey = groupProtectionService.unprotect(group, 
        protection.getPassword());
    
    credential.getPrivateKey().setContent(wrapCredentialPrivateKey(privateKey, secretKey)
    .getContent());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper unprotect(Credential credential,
      ProtectionParameters protection) throws UserAccessException,
      GroupAccessException {
    
    try {
      UserGroup group = findGroup(protection.getGroupName());
      if (!group.equals(credential.getOwner())) {
        throw new GroupAccessException(protection.getGroupName() 
            + " is not the owner of " + credential.getName());
      }
  
      SecretKey secretKey = groupProtectionService.unprotect(group, 
          protection.getPassword());
  
      return unwrapCredentialPrivateKey(credential.getPrivateKey().getContent(), secretKey);
    }
    catch (NoSuchGroupException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * Finds a group by name.
   * @param groupName name of the group to match
   * @return group object
   * @throws NoSuchGroupException if the group does not exist
   */
  private UserGroup findGroup(String groupName) 
      throws NoSuchGroupException {
    UserGroup group = groupRepository.findByGroupName(groupName, 
        userContextService.getLoginName());
    if (group == null) {
      throw new NoSuchGroupException();
    }
    return group;
  }

  private PrivateKeyWrapper wrapCredentialPrivateKey(
      PrivateKeyWrapper privateKey, SecretKey secretKey) {
    return privateKeyEncryptionService.encrypt(privateKey, secretKey);
  }

  private PrivateKeyWrapper unwrapCredentialPrivateKey(
      String encodedPrivateKey, SecretKey groupSecretKey) {

    PrivateKeyWrapper encryptedCredentialKey = aesDecoder.decode(
        encodedPrivateKey);
    
    encryptedCredentialKey.setProtectionParameter(
        groupSecretKey);
    
    return encryptedCredentialKey.deriveWrapper();
  }
  
}
