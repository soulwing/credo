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

import java.security.PublicKey;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserGroupMemberBuilder;
import org.soulwing.credo.UserGroupMemberBuilderFactory;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.UserProfileBuilder;
import org.soulwing.credo.UserProfileBuilderFactory;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.repository.UserProfileRepository;
import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.crypto.PKCS8EncryptionService;
import org.soulwing.credo.service.crypto.PasswordEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.PublicKeyWrapper;
import org.soulwing.credo.service.crypto.SecretKeyEncryptionService;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

/**
 * Unit tests for {@link ConcreteUserProfileService}.
 *
 * @author Carl Harris
 */
public class ConcreteUserProfileServiceTest {

  private static final String LOGIN_NAME = "someUser";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  private final String loginName = new String();
  private final String fullName = new String();
  private final Password password = new Password(new char[0]);
  private final String encryptedPassword = new String();
  private final String encodedPublicKey = new String();
  private final String encodedPrivateKey = new String();
  private final String encodedSecretKey = new String();

  @Mock
  private UserProfileRepository profileRepository;
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private UserGroupMemberRepository groupMemberRepository;
  
  @Mock
  private UserProfileBuilderFactory profileBuilderFactory;
  
  @Mock
  private UserGroupMemberBuilderFactory groupMemberBuilderFactory;
  
  @Mock
  private KeyGeneratorService keyGeneratorService;
  
  @Mock
  private PasswordEncryptionService passwordEncryptionService;
  
  @Mock
  private PKCS8EncryptionService privateKeyEncryptionService;
  
  @Mock
  private SecretKeyEncryptionService secretKeyEncryptionService;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  private KeyPairWrapper keyPair;
  
  @Mock
  private PublicKeyWrapper publicKey;
  
  @Mock
  private PublicKey jcaPublicKey;
  
  @Mock
  private PrivateKeyWrapper privateKey;

  @Mock
  private SecretKeyWrapper secretKey;
  
  @Mock
  private PrivateKeyWrapper encryptedPrivateKey;

  @Mock
  private SecretKeyWrapper encryptedSecretKey;
  
  @Mock
  private UserProfilePreparation preparation;
  
  @Mock
  private UserProfileBuilder profileBuilder;
  
  @Mock
  private UserGroupMemberBuilder groupMemberBuilder;
  
  @Mock
  private UserProfile user;
  
  @Mock
  private UserGroup group;
  
  @Mock
  private UserGroupMember groupMember;
  
  private ConcreteUserProfileService service = new ConcreteUserProfileService();
  
  @Before
  public void setUp() throws Exception {
    service.profileRepository = profileRepository;
    service.groupRepository = groupRepository;
    service.groupMemberRepository = groupMemberRepository;
    service.groupMemberBuilderFactory = groupMemberBuilderFactory;
    service.profileBuilderFactory = profileBuilderFactory;
    service.keyGeneratorService = keyGeneratorService;
    service.passwordEncryptionService = passwordEncryptionService;
    service.privateKeyEncryptionService = privateKeyEncryptionService;
    service.secretKeyEncryptionService = secretKeyEncryptionService;
    service.userContextService = userContextService;
  }
  
  @Test
  public void testIsNewUserWithNewUser() throws Exception {
    context.checking(new Expectations() { {
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(profileRepository).findByLoginName(with(same(LOGIN_NAME)));
      will(returnValue(null));
    } });
    
    assertThat(service.isNewUser(), is(true));
  }

  @Test
  public void testIsNewUserWithExistingUser() throws Exception {
    final UserProfile profile = context.mock(UserProfile.class);
    context.checking(new Expectations() { { 
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(profileRepository).findByLoginName(with(same(LOGIN_NAME)));
      will(returnValue(profile));
    } });
    
    assertThat(service.isNewUser(), is(false));
  }
  

  @Test
  public void testPrepareProfile() throws Exception {
    UserProfilePreparation preparation = service.prepareProfile(LOGIN_NAME);
    assertThat(preparation, is(not(nullValue())));
    assertThat(preparation, hasProperty("loginName", equalTo(LOGIN_NAME)));
  }
  
  @Test
  public void testCreateProfile() throws Exception {
    context.checking(preparationExpectations());
    context.checking(keyGenerationExpectations());
    context.checking(encryptionExpectations());
    context.checking(profileExpectations());
    context.checking(groupExpectations());
    context.checking(groupMemberExpectations());
    context.checking(repositoryExpectations());    
    service.createProfile(preparation);
  }

  private Expectations preparationExpectations() {
    return new Expectations() { { 
      oneOf(preparation).getLoginName();
      will(returnValue(loginName));
      oneOf(preparation).getFullName();
      will(returnValue(fullName));
      allowing(preparation).getPassword();
      will(returnValue(password));
    } };
  }

  private Expectations keyGenerationExpectations() {
    return new Expectations() { {
      oneOf(keyGeneratorService).generateKeyPair();
      will(returnValue(keyPair));
      oneOf(keyPair).getPublic();
      will(returnValue(publicKey));
      oneOf(keyPair).getPrivate();
      will(returnValue(privateKey));
      oneOf(publicKey).getContent();
      will(returnValue(encodedPublicKey));
      oneOf(keyGeneratorService).generateSecretKey();
      will(returnValue(secretKey));
    } };
  }

  private Expectations encryptionExpectations() {
    return new Expectations() { { 
      oneOf(passwordEncryptionService).encrypt(with(same(password)));
      will(returnValue(encryptedPassword));
      oneOf(privateKeyEncryptionService).encrypt(with(same(privateKey)),
          with(same(password)));
      will(returnValue(encryptedPrivateKey));
      oneOf(encryptedPrivateKey).getContent();
      will(returnValue(encodedPrivateKey));
      oneOf(publicKey).derive();
      will(returnValue(jcaPublicKey));
      oneOf(secretKeyEncryptionService).encrypt(with(same(secretKey)),
          with(same(jcaPublicKey)));
      will(returnValue(encryptedSecretKey));
      oneOf(encryptedSecretKey).getContent();
      will(returnValue(encodedSecretKey));
    } };
  }

  private Expectations profileExpectations() {
    return new Expectations() { { 
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
      will(returnValue(user));
    } };
  }

  private Expectations groupExpectations() {
    return new Expectations() { { 
      oneOf(groupRepository).newGroup(with(UserGroup.SELF_GROUP_NAME));
      will(returnValue(group));
    } };
  }
  
  private Expectations groupMemberExpectations() {
    return new Expectations() { { 
      oneOf(groupMemberBuilderFactory).newBuilder();
      will(returnValue(groupMemberBuilder));
      oneOf(groupMemberBuilder).setUser(with(same(user)));
      will(returnValue(groupMemberBuilder));
      oneOf(groupMemberBuilder).setGroup(with(same(group)));
      will(returnValue(groupMemberBuilder));
      oneOf(groupMemberBuilder).setSecretKey(with(any(String.class)));  //FIXME
      will(returnValue(groupMemberBuilder));
      oneOf(groupMemberBuilder).build();
      will(returnValue(groupMember));
    } };
  }

  private Expectations repositoryExpectations() {
    return new Expectations() { {
      oneOf(profileRepository).add(with(same(user)));
      oneOf(groupRepository).add(with(same(group)));
      oneOf(groupMemberRepository).add(with(same(groupMember)));
    } };
  }
  
}
