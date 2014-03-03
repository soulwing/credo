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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.UserProfileBuilderFactory;
import org.soulwing.credo.repository.UserProfileRepository;
import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.crypto.PasswordEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;

/**
 * A concrete {@link UserProfileService} implementation.
 *
 * @author Carl Harris
 */
@ApplicationScoped
@Transactional
public class ConcreteUserProfileService 
    implements WelcomeService, UserProfileService {

  @Inject
  protected UserProfileRepository profileRepository;
  
  @Inject
  protected UserProfileBuilderFactory profileBuilderFactory;
  
  @Inject
  protected PasswordEncryptionService passwordEncryptionService;
  
  @Inject
  protected PrivateKeyEncryptionService privateKeyEncryptionService;
  
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
    
    UserProfile profile = profileBuilderFactory.newProfileBuilder()
        .setLoginName(preparation.getLoginName())
        .setFullName(preparation.getFullName())
        .setPassword(passwordEncryptionService.encrypt(
            preparation.getPassword()))
        .setPublicKey(keyPair.getPublic().getContent())
        .setPrivateKey(privateKeyEncryptionService.encrypt(
            keyPair.getPrivate(), 
            preparation.getPassword()).getContent())
        .build();
    
    profileRepository.add(profile);
  }

}
