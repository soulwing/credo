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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.UserProfileBuilder;
import org.soulwing.credo.UserProfileBuilderFactory;
import org.soulwing.credo.repository.UserProfileRepository;
import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.crypto.PasswordEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.PublicKeyWrapper;

/**
 * Unit tests for {@link ConcreteUserProfileService}.
 *
 * @author Carl Harris
 */
public class ConcreteUserProfileServiceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private UserProfileRepository profileRepository;
  
  @Mock
  private UserProfileBuilderFactory profileBuilderFactory;
  
  @Mock
  private KeyGeneratorService keyGeneratorService;
  
  @Mock
  private PasswordEncryptionService passwordEncryptionService;
  
  @Mock
  private PrivateKeyEncryptionService privateKeyEncryptionService;
  
  @Mock
  private KeyPairWrapper keyPair;
  
  @Mock
  private PublicKeyWrapper publicKey;
  
  @Mock
  private PrivateKeyWrapper privateKey;

  @Mock
  private PrivateKeyWrapper encryptedPrivateKey;

  @Mock
  private UserProfilePreparation preparation;
  
  @Mock
  private UserProfileBuilder profileBuilder;
  
  @Mock
  private UserProfile profile;
  
  private ConcreteUserProfileService service = new ConcreteUserProfileService();
  
  @Before
  public void setUp() throws Exception {
    service.profileRepository = profileRepository;
    service.profileBuilderFactory = profileBuilderFactory;
    service.keyGeneratorService = keyGeneratorService;
    service.passwordEncryptionService = passwordEncryptionService;
    service.privateKeyEncryptionService = privateKeyEncryptionService;
  }
  
  @Test
  public void testIsNewUserWithNewUser() throws Exception {
    final String loginName = "someUser";
    context.checking(new Expectations() { { 
      oneOf(profileRepository).findByLoginName(with(same(loginName)));
      will(returnValue(null));
    } });
    
    assertThat(service.isNewUser(loginName), is(true));
  }

  @Test
  public void testIsNewUserWithExistingUser() throws Exception {
    final String loginName = "someUser";
    final UserProfile profile = context.mock(UserProfile.class);
    context.checking(new Expectations() { { 
      oneOf(profileRepository).findByLoginName(with(same(loginName)));
      will(returnValue(profile));
    } });
    
    assertThat(service.isNewUser(loginName), is(false));
  }
  

  @Test
  public void testPrepareProfile() throws Exception {
    UserProfilePreparation preparation = service.prepareProfile("someUser");
    assertThat(preparation, is(not(nullValue())));
    assertThat(preparation, hasProperty("loginName", equalTo("someUser")));
  }
  
  @Test
  public void testCreateProfile() throws Exception {
    final String loginName = new String();
    final String fullName = new String();
    final char[] password = new char[0];
    final String encryptedPassword = new String();
    final String encodedPublicKey = new String();
    final String encodedPrivateKey = new String();
    context.checking(new Expectations() { {
      oneOf(preparation).getLoginName();
      will(returnValue(loginName));
      oneOf(preparation).getFullName();
      will(returnValue(fullName));
      allowing(preparation).getPassword();
      will(returnValue(password));
      oneOf(keyGeneratorService).generateKeyPair();
      will(returnValue(keyPair));
      oneOf(keyPair).getPublic();
      will(returnValue(publicKey));
      oneOf(keyPair).getPrivate();
      will(returnValue(privateKey));
      oneOf(passwordEncryptionService).encrypt(with(same(password)));
      will(returnValue(encryptedPassword));
      oneOf(privateKeyEncryptionService).encrypt(with(same(privateKey)),
          with(same(password)));
      will(returnValue(encryptedPrivateKey));
      oneOf(publicKey).getContent();
      will(returnValue(encodedPublicKey));
      oneOf(encryptedPrivateKey).getContent();
      will(returnValue(encodedPrivateKey));
      oneOf(profileBuilderFactory).newProfileBuilder();
      will(returnValue(profileBuilder));
      oneOf(profileBuilder).setLoginName(with(same(loginName)));
      will(returnValue(profileBuilder));
      oneOf(profileBuilder).setFullName(with(same(fullName)));
      will(returnValue(profileBuilder));
      oneOf(profileBuilder).setPassword(with(same(encryptedPassword)));
      will(returnValue(profileBuilder));
      oneOf(profileBuilder).setPublicKey(with(same(encodedPublicKey)));
      will(returnValue(profileBuilder));
      oneOf(profileBuilder).setPrivateKey(with(same(encodedPrivateKey)));
      will(returnValue(profileBuilder));
      oneOf(profileBuilder).build();
      will(returnValue(profile));
      oneOf(profileRepository).add(with(same(profile)));
    } });
    
    service.createProfile(preparation);
  }
  
}
