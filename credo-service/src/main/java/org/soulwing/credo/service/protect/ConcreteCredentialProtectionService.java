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

import java.security.PrivateKey;

import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.crypto.Encoded;
import org.soulwing.credo.service.crypto.Encoded.Type;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.SecretKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

/**
 * A concrete {@link CredentialProtectionService} implementation.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteCredentialProtectionService
    implements CredentialProtectionService {

  @Inject
  protected UserGroupMemberRepository groupMemberRepository; 
  
  @Inject
  protected UserContextService userContextService;
  
  @Inject
  protected PrivateKeyEncryptionService privateKeyEncryptionService;
  
  @Inject @Encoded(type = Type.PKCS8)
  protected PrivateKeyDecoder pkcs8Decoder;
  
  @Inject @Encoded(type = Type.AES)
  protected PrivateKeyDecoder aesDecoder;
  
  @Inject
  protected SecretKeyDecoder secretKeyDecoder;

  /**
   * {@inheritDoc}
   */
  @Override
  public void protect(Credential credential, PrivateKeyWrapper privateKey,
      ProtectionParameters protection) throws UserAccessException,
      GroupAccessException {
    
    UserGroupMember groupMember = findGroupMember(
        protection.getGroupName(), userContextService.getLoginName());
    
    credential.getPrivateKey().setContent(protect(privateKey, groupMember, 
        protection.getPassword()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper unprotect(Credential credential,
      ProtectionParameters protection) throws UserAccessException,
      GroupAccessException {
    UserGroupMember groupMember = findGroupMember(
        protection.getGroupName(), userContextService.getLoginName());
    if (!groupMember.getGroup().equals(credential.getOwner())) {
      throw new GroupAccessException(protection.getGroupName() 
          + " is not the owner of " + credential.getName());
    }
    return unprotect(credential.getPrivateKey().getContent(), groupMember, 
        protection.getPassword());
  }
  
  private UserGroupMember findGroupMember(String groupName, 
      String loginName) throws GroupAccessException {
    UserGroupMember groupMember = groupMemberRepository
        .findByGroupAndLoginName(groupName, loginName);
    if (groupMember == null) {
      throw new GroupAccessException(
          loginName + " is not a member of group " + groupName);
    }
    return groupMember;
  }

  private String protect(PrivateKeyWrapper privateKey, 
      UserGroupMember groupMember, Password password) 
      throws UserAccessException {
    
    try {
      PrivateKey userPrivateKey = 
          unwrapUserPrivateKey(groupMember, password);
  
      SecretKey groupSecretKey =
          unwrapGroupSecretKey(groupMember, userPrivateKey);
      
      return wrapCredentialPrivateKey(privateKey, groupSecretKey)
          .getContent();
      
    }
    catch (IncorrectPassphraseException ex) {
      throw new UserAccessException(ex);
    }
  }
  
  private PrivateKeyWrapper unprotect(String encodedPrivateKey, 
      UserGroupMember groupMember, Password password) 
      throws UserAccessException {
    
    try {
      PrivateKey userPrivateKey = 
          unwrapUserPrivateKey(groupMember, password);
  
      SecretKey groupSecretKey =
          unwrapGroupSecretKey(groupMember, userPrivateKey);
      
      return unwrapCredentialPrivateKey(encodedPrivateKey, groupSecretKey);
      
    }
    catch (IncorrectPassphraseException ex) {
      throw new UserAccessException(ex);
    }
  }

  private PrivateKey unwrapUserPrivateKey(UserGroupMember groupMember,
      Password password) {
    
    PrivateKeyWrapper encryptedPrivateKey = pkcs8Decoder.decode(
        groupMember.getUser().getPrivateKey());
    
    encryptedPrivateKey.setProtectionParameter(password);
    
    return encryptedPrivateKey.derive();
  }

  private SecretKey unwrapGroupSecretKey(UserGroupMember groupMember,
      PrivateKey privateKey) {
    
    SecretKeyWrapper encryptedSecretKey = secretKeyDecoder.decode(
        groupMember.getSecretKey());     
    
    encryptedSecretKey.setPrivateKey(privateKey);
    
    return encryptedSecretKey.derive();
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
