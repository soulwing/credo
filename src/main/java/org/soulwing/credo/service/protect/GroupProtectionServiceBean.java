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
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.crypto.Encoded;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.PublicKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyEncryptionService;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.crypto.WrappedWith;

/**
 * A concrete {@link GroupProtectionService} implementation.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class GroupProtectionServiceBean
    implements GroupProtectionService {

  @Inject
  protected PublicKeyDecoder publicKeyDecoder;
  
  @Inject @Encoded(Encoded.Type.PKCS8)
  protected PrivateKeyDecoder pkcs8Decoder;
  
  @Inject
  protected SecretKeyDecoder secretKeyDecoder;

  @Inject @WrappedWith(WrappedWith.Type.RSA)
  protected SecretKeyEncryptionService rsaSecretKeyEncryptionService;
  
  @Inject
  protected UserGroupMemberBuilderFactory memberBuilderFactory;
  
  @Inject
  protected UserGroupMemberRepository memberRepository;
  
  @Inject
  protected UserContextService userContextService;
  
  @Inject
  protected PrivateKeyHolder privateKeyHolder;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void protect(UserGroup group, SecretKeyWrapper secretKey,
      UserProfile profile) {
    PublicKey publicKey = publicKeyDecoder.decode(profile.getPublicKey())
        .derive();
    SecretKeyWrapper encryptedSecretKey = 
        rsaSecretKeyEncryptionService.encrypt(secretKey, publicKey);
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
    UserGroupMember member = findGroupMember(group, 
        userContextService.getLoginName());
    if (member == null) {
      throw new GroupAccessException(group.getName());
    }
    return unwrapSecretKey(group, member, unwrapPrivateKey(member, password));        
  }

  /**
   * Finds a group member entity for a given group and user.
   * <p>
   * The group and owner ancestry are searched to find a member that matches
   * the given login name.
   * 
   * @param group the group to search 
   * @param loginName subject user login name
   * @return group member entity
   * @throws GroupAccessException if the user is not a member of the group or
   *    any of its owner ancestors
   */
  private UserGroupMember findGroupMember(UserGroup group,
      String loginName) {
    UserGroupMember groupMember = memberRepository
        .findByGroupNameAndLoginName(group.getName(), loginName);
    if (groupMember == null && group.getOwner() != null) {
      return findGroupMember(group.getOwner(), loginName);
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
    
    PrivateKey privateKey = privateKeyHolder.getPrivateKey();
    if (privateKey == null) {
      PrivateKeyWrapper encryptedPrivateKey = pkcs8Decoder.decode(
          member.getUser().getPrivateKey());
      
      encryptedPrivateKey.setProtectionParameter(password);
      
      try {
        privateKey = encryptedPrivateKey.derive();
        privateKeyHolder.setPrivateKey(privateKey);
      }
      catch (IncorrectPassphraseException ex) {
        throw new UserAccessException(ex);
      }
    }
    
    return privateKey;

  }

  /**
   * Unwraps (decrypts) the secret key for a given group.
   * @param group the subject group
   * @param member a member of group or one of its owner ancestors
   * @param privateKey (unwrapped) private key of {@code member} 
   * @return unwrapped secret key
   */
  private SecretKeyWrapper unwrapSecretKey(UserGroup group,
      UserGroupMember member, PrivateKey privateKey) {
    
    SecretKeyWrapper encryptedSecretKey = null;
    if (group.equals(member.getGroup())) {
      encryptedSecretKey = secretKeyDecoder.decode(member.getSecretKey());      
      encryptedSecretKey.setKey(privateKey);
    }
    else {
      encryptedSecretKey = secretKeyDecoder.decode(group.getSecretKey());           
      encryptedSecretKey.setKey(
          unwrapSecretKey(group.getOwner(), member, privateKey).derive());
    }
    
    return encryptedSecretKey.deriveWrapper();
  }

}
