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

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserGroupMemberBuilderFactory;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.crypto.Encoded;
import org.soulwing.credo.service.crypto.Encoded.Type;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.PublicKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyEncryptionService;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

/**
 * A concrete {@link GroupProtectionService} implementation.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteGroupProtectionService
    implements GroupProtectionService {

  @Inject
  protected PublicKeyDecoder publicKeyDecoder;
  
  @Inject @Encoded(Type.PKCS8)
  protected PrivateKeyDecoder pkcs8Decoder;
  
  @Inject
  protected SecretKeyDecoder secretKeyDecoder;

  @Inject
  protected SecretKeyEncryptionService secretKeyEncryptionService;
  
  @Inject
  protected UserGroupMemberBuilderFactory memberBuilderFactory;
  
  @Inject
  protected UserGroupMemberRepository memberRepository;
  
  @Inject
  protected UserContextService userContextService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void protect(UserGroup group, SecretKeyWrapper secretKey,
      UserProfile profile) {
    PublicKey publicKey = publicKeyDecoder.decode(profile.getPublicKey())
        .derive();
    SecretKeyWrapper encryptedSecretKey = 
        secretKeyEncryptionService.encrypt(secretKey, publicKey);
    UserGroupMember member = memberBuilderFactory.newBuilder()
        .setGroup(group)
        .setUser(profile)
        .setSecretKey(encryptedSecretKey.getContent())
        .build();
    memberRepository.add(member);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKeyWrapper unprotect(UserGroup group, Password password) 
      throws GroupAccessException, UserAccessException {
    UserGroupMember member = findGroupMember(group.getName(), 
        userContextService.getLoginName());
    return unwrapSecretKey(member, unwrapPrivateKey(member, password));
  }

  /**
   * Finds a group member entity for a given group and user.
   * @param groupName subject group name 
   * @param loginName subject user login name
   * @return group member entity
   * @throws GroupAccessException if the user is not a member of the group
   */
  private UserGroupMember findGroupMember(String groupName, 
      String loginName) throws GroupAccessException {
    UserGroupMember groupMember = memberRepository
        .findByGroupAndLoginName(groupName, loginName);
    if (groupMember == null) {
      throw new GroupAccessException(
          loginName + " is not a member of group " + groupName);
    }
    return groupMember;
  }

  /**
   * Unwraps (decrypts) the private key of a group member.
   * @param member the subject group member
   * @param password password provided by the subject user
   * @return unwrapped private key
   * @throws UserAccessException if the private key could not be unwrapped
   *    using the given password
   */
  private PrivateKey unwrapPrivateKey(UserGroupMember member,
      Password password) throws UserAccessException {
    
    PrivateKeyWrapper encryptedPrivateKey = pkcs8Decoder.decode(
        member.getUser().getPrivateKey());
    
    encryptedPrivateKey.setProtectionParameter(password);
    
    try {
      return encryptedPrivateKey.derive();
    }
    catch (IncorrectPassphraseException ex) {
      throw new UserAccessException(ex);
    }

  }

  /**
   * Unwraps (decrypts) the secret key associated with a given group member.
   * @param member the subject group member
   * @param privateKey (unwrapped) private key of the group member 
   * @return unwrapped secret key
   */
  private SecretKeyWrapper unwrapSecretKey(UserGroupMember member,
      PrivateKey privateKey) {
    
    SecretKeyWrapper encryptedSecretKey = secretKeyDecoder.decode(
        member.getSecretKey());     
    
    encryptedSecretKey.setPrivateKey(privateKey);
    
    return encryptedSecretKey.deriveWrapper();
  }

}
