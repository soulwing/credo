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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.jmock.Expectations;
import org.jmock.api.Action;
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
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.PublicKeyDecoder;
import org.soulwing.credo.service.crypto.PublicKeyWrapper;
import org.soulwing.credo.service.crypto.SecretKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyEncryptionService;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

/**
 * Unit tests for {@link ConcreteGroupProtectionServiceTest}.
 *
 * @author Carl Harris
 */
public class ConcreteGroupProtectionServiceTest {

  private static final Password PASSWORD = new Password(new char[0]);
  
  private static final String GROUP_NAME = "someGroup";
  
  private static final String LOGIN_NAME = "someUser";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private PublicKeyDecoder publicKeyDecoder;
  
  @Mock
  private PrivateKeyDecoder pkcs8Decoder;
  
  @Mock
  private SecretKeyDecoder secretKeyDecoder;
  
  @Mock
  private SecretKeyEncryptionService secretKeyEncryptionService;
  
  @Mock
  private UserGroupMemberBuilderFactory memberBuilderFactory;
  
  @Mock
  private UserGroupMemberRepository memberRepository;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  private UserGroup group;
  
  @Mock
  private UserProfile profile;
  
  @Mock
  private UserGroupMember member;
  
  @Mock
  private UserGroupMemberBuilder memberBuilder;

  @Mock
  private SecretKeyWrapper secretKeyWrapper;
  
  @Mock
  private PublicKeyWrapper publicKeyWrapper;
  
  @Mock
  private PrivateKeyWrapper privateKeyWrapper;
  
  @Mock
  private PublicKey publicKey;
  
  @Mock
  private PrivateKey privateKey;
  

  private ConcreteGroupProtectionService service = 
      new ConcreteGroupProtectionService();
  
  @Before
  public void setUp() throws Exception {
    service.publicKeyDecoder = publicKeyDecoder;
    service.pkcs8Decoder = pkcs8Decoder;
    service.secretKeyDecoder = secretKeyDecoder;
    service.secretKeyEncryptionService = secretKeyEncryptionService;
    service.memberBuilderFactory = memberBuilderFactory;
    service.memberRepository = memberRepository;
    service.userContextService = userContextService;
  }
  
  @Test
  public void testProtect() throws Exception {
    context.checking(publicKeyExpectations());
    context.checking(secretKeyExpectationsOnProtect());
    context.checking(memberExpectationsOnProtect());
    service.protect(group, secretKeyWrapper, profile);
  }
  
  @Test
  public void testUnprotect() throws Exception {
    context.checking(memberExpectationsOnUnprotect(returnValue(member)));
    context.checking(privateKeyExpectations(returnValue(privateKey)));
    context.checking(secretKeyExpectationsOnUnprotect());
    assertThat(service.unprotect(group, PASSWORD), 
        is(sameInstance(secretKeyWrapper)));
  }
  
  @Test(expected = GroupAccessException.class)
  public void testUnprotectWhenUserNotMember() throws Exception {
    context.checking(memberExpectationsOnUnprotect(returnValue(null)));
    service.unprotect(group, PASSWORD);
  }
  
  @Test(expected = UserAccessException.class)
  public void testUnprotectWhenPasswordIncorrect() throws Exception {
    context.checking(memberExpectationsOnUnprotect(returnValue(member)));
    context.checking(privateKeyExpectations(
        throwException(new IncorrectPassphraseException())));
    service.unprotect(group, PASSWORD);
  }
  

  private Expectations publicKeyExpectations() {
    final String encodedPublicKey = new String();
    return new Expectations() { { 
      oneOf(profile).getPublicKey();
      will(returnValue(encodedPublicKey));
      oneOf(publicKeyDecoder).decode(with(same(encodedPublicKey)));
      will(returnValue(publicKeyWrapper));
      oneOf(publicKeyWrapper).derive();
      will(returnValue(publicKey));
    } };
  }
  
  private Expectations secretKeyExpectationsOnProtect() {
    return new Expectations() { { 
      oneOf(secretKeyEncryptionService).encrypt(with(same(secretKeyWrapper)), 
          with(same(publicKey)));
      will(returnValue(secretKeyWrapper));
    } };    
  }
  
  private Expectations memberExpectationsOnProtect() {
    final String encodedSecretKey = new String();
    return new Expectations() { { 
      oneOf(secretKeyWrapper).getContent();
      will(returnValue(encodedSecretKey));
      oneOf(memberBuilderFactory).newBuilder();
      will(returnValue(memberBuilder));
      oneOf(memberBuilder).setGroup(with(same(group)));
      will(returnValue(memberBuilder));
      oneOf(memberBuilder).setUser(with(same(profile)));
      will(returnValue(memberBuilder));      
      oneOf(memberBuilder).setSecretKey(with(encodedSecretKey));
      will(returnValue(memberBuilder));
      oneOf(memberBuilder).build();
      will(returnValue(member));
      oneOf(memberRepository).add(member);
    } };
  }
   
  private Expectations privateKeyExpectations(final Action outcome) {
    final String encodedPrivateKey = new String();
    return new Expectations() { {
      oneOf(member).getUser();
      will(returnValue(profile));
      oneOf(profile).getPrivateKey();
      will(returnValue(encodedPrivateKey));
      oneOf(pkcs8Decoder).decode(with(same(encodedPrivateKey)));
      will(returnValue(privateKeyWrapper));
      oneOf(privateKeyWrapper).setProtectionParameter(with(same(PASSWORD)));
      oneOf(privateKeyWrapper).derive();
      will(outcome);
    } };
  }
  
  private Expectations secretKeyExpectationsOnUnprotect() {
    final String encodedSecretKey = new String();
    return new Expectations() { { 
      oneOf(member).getSecretKey();
      will(returnValue(encodedSecretKey));
      oneOf(secretKeyDecoder).decode(with(same(encodedSecretKey)));
      will(returnValue(secretKeyWrapper));
      oneOf(secretKeyWrapper).setPrivateKey(with(same(privateKey)));
      oneOf(secretKeyWrapper).deriveWrapper();
      will(returnValue(secretKeyWrapper));
    } };
  }
  
  private Expectations memberExpectationsOnUnprotect(final Action outcome) {
    return new Expectations() { {
      oneOf(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(memberRepository).findByGroupNameAndLoginName(
          with(same(GROUP_NAME)), with(same(LOGIN_NAME)));
      will(outcome);
    } };
  }

}
