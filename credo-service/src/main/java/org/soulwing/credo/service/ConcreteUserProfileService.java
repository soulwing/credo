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
package org.soulwing.credo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserGroupMemberBuilderFactory;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.UserProfileBuilderFactory;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.repository.UserProfileRepository;
import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.crypto.PKCS8EncryptionService;
import org.soulwing.credo.service.crypto.PasswordEncryptionService;
import org.soulwing.credo.service.crypto.PublicKeyWrapper;
import org.soulwing.credo.service.crypto.SecretKeyEncryptionService;

/**
 * A concrete {@link UserProfileService} implementation.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConcreteUserProfileService 
    implements WelcomeService, UserProfileService {

  @Inject
  protected UserProfileRepository profileRepository;
  
  @Inject
  protected UserGroupRepository groupRepository;
  
  @Inject
  protected UserGroupMemberRepository groupMemberRepository;
  
  @Inject
  protected UserProfileBuilderFactory profileBuilderFactory;
  
  @Inject
  protected UserGroupMemberBuilderFactory groupMemberBuilderFactory;
  
  @Inject
  protected UserContextService userContextService;
  
  @Inject
  protected PasswordEncryptionService passwordEncryptionService;
  
  @Inject
  protected PKCS8EncryptionService privateKeyEncryptionService;
  
  @Inject
  protected SecretKeyEncryptionService secretKeyEncryptionService;
  
  @Inject
  protected KeyGeneratorService keyGeneratorService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNewUser(String loginName) {
    return profileRepository.findByLoginName(loginName) == null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfile getLoggedInUserProfile() {
    String loginName = userContextService.getLoginName();
    UserProfile profile = findProfile(loginName);
    if (profile == null) {
      throw new IllegalStateException("no such user: " + loginName);
    }
    return profile;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfile findProfile(String loginName) {
    return profileRepository.findByLoginName(loginName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<UserDetail> findAllProfiles() {
    List<UserProfile> profiles = profileRepository.findAll();
    Collection<UserDetail> details = new ArrayList<>(profiles.size());
    for (UserProfile profile : profiles) {
      details.add(new UserProfileWrapper(profile));
    }
    return details;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfilePreparation prepareProfile(String loginName) {
    return new ConcreteUserProfilePreparation(loginName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createProfile(UserProfilePreparation preparation) {
    Validate.notNull(preparation.getPassword(), "password is required");
    KeyPairWrapper keyPair = keyGeneratorService.generateKeyPair();    
    PublicKeyWrapper publicKey = keyPair.getPublic();
    
    UserProfile user = profileBuilderFactory.newProfileBuilder()
        .setLoginName(preparation.getLoginName())
        .setFullName(preparation.getFullName())
        .setPassword(passwordEncryptionService.encrypt(
            preparation.getPassword()))
        .setPublicKey(publicKey.getContent())
        .setPrivateKey(privateKeyEncryptionService.encrypt(
            keyPair.getPrivate(), 
            preparation.getPassword()).getContent())
        .build();
    
    UserGroup group = groupRepository.newGroup(UserGroup.SELF_GROUP_NAME);
    
    UserGroupMember groupMember = groupMemberBuilderFactory.newBuilder()
        .setUser(user)
        .setGroup(group)
        .setSecretKey(secretKeyEncryptionService.encrypt(
            keyGeneratorService.generateSecretKey(), 
            publicKey.derive()).getContent())
        .build();
    
    profileRepository.add(user);
    groupRepository.add(group);
    groupMemberRepository.add(groupMember);
  }

  private static class UserProfileWrapper implements UserDetail {

    private final UserProfile delegate;
    
    /**
     * Constructs a new instance.
     * @param delegate
     */
    public UserProfileWrapper(UserProfile delegate) {
      this.delegate = delegate;
    }

    @Override
    public Long getId() {
      return delegate.getId();
    }

    @Override
    public String getLoginName() {
      return delegate.getLoginName();
    }

    @Override
    public String getFullName() {
      return delegate.getFullName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return String.format("%s (%s)", getFullName(), getLoginName());
    }
    
  }
  

}
