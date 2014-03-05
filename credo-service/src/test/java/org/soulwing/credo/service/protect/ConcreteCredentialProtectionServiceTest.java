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

import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.security.PrivateKey;

import javax.crypto.SecretKey;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialKey;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.SecretKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

/**
 * Unit tests for {@link ConcreteCredentialProtectionService}.
 *
 * @author Carl Harris
 */
public class ConcreteCredentialProtectionServiceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  private final String loginName = "someUser";
  
  private final String groupName = "someGroup";

  private final char[] password = "somePassword".toCharArray();

  private final String encodedPrivateKey = "privateKey";
  
  private final String encodedSecretKey = "secretKey";
  
  @Mock
  private UserGroupMemberRepository groupMemberRepository; 
  
  @Mock
  private PrivateKeyDecoder pkcs8Decoder;
  
  @Mock
  private PrivateKeyDecoder aesDecoder;
  
  @Mock
  private SecretKeyDecoder secretKeyDecoder;

  @Mock
  private PrivateKeyEncryptionService privateKeyEncryptionService;
  
  @Mock
  private Credential credential;

  @Mock
  private ProtectionParameters protection;
  
  @Mock
  private CredentialKey credentialKey;

  @Mock
  private UserGroup group;

  @Mock
  private UserProfile user;
  
  @Mock
  private UserGroupMember groupMember;
  
  @Mock
  private PrivateKeyWrapper encryptedUserPrivateKey;
  
  @Mock
  private SecretKeyWrapper encryptedSecretKey;
  
  @Mock
  private PrivateKeyWrapper encryptedCredentialPrivateKey;
  
  @Mock
  private PrivateKey userPrivateKey;
  
  @Mock
  private SecretKey groupSecretKey;
  
  @Mock
  private PrivateKeyWrapper credentialPrivateKey;
  
  private ConcreteCredentialProtectionService service = 
      new ConcreteCredentialProtectionService();
  
  @Before
  public void setUp() throws Exception {
    service.groupMemberRepository = groupMemberRepository;
    service.pkcs8Decoder = pkcs8Decoder;
    service.aesDecoder = aesDecoder;
    service.secretKeyDecoder = secretKeyDecoder;
    service.privateKeyEncryptionService = privateKeyEncryptionService;
  }

  @Test
  public void testProtectSuccess() throws Exception {
    context.checking(findGroupMemberExpectations(returnValue(groupMember)));
    context.checking(accessGroupMemberExpectations());
    context.checking(accessCredentialKeyExpectations());
    context.checking(unwrapUserPrivateKeyExpectations(
        returnValue(userPrivateKey)));
    context.checking(unwrapGroupSecretKeyExpectations());
    context.checking(wrapCredentialPrivateKeyExpectations());
    context.checking(storeCredentialPrivateKeyExpectations());
    service.protect(credential, credentialPrivateKey, protection);
  }
  
  @Test(expected = GroupAccessException.class)
  public void testProtectWhenNotGroupMember() throws Exception {
    context.checking(findGroupMemberExpectations(returnValue(null)));
    service.protect(credential, credentialPrivateKey, protection);
  }
  
  @Test(expected = UserAccessException.class)
  public void testProtectWhenPasswordIncorrect() throws Exception {
    context.checking(findGroupMemberExpectations(returnValue(groupMember)));
    context.checking(accessGroupMemberExpectations());
    context.checking(accessCredentialKeyExpectations());
    context.checking(unwrapUserPrivateKeyExpectations(
        throwException(new IncorrectPassphraseException())));
    service.protect(credential, credentialPrivateKey, protection);
  }
  
  @Test
  public void testUnprotectSuccess() throws Exception {
    context.checking(findGroupMemberExpectations(returnValue(groupMember)));
    context.checking(accessGroupMemberExpectations());
    context.checking(accessCredentialKeyExpectations());
    context.checking(unwrapUserPrivateKeyExpectations(
        returnValue(userPrivateKey)));
    context.checking(unwrapGroupSecretKeyExpectations());
    context.checking(unwrapCredentialPrivateKeyExpectations());
    service.unprotect(credential, protection);
  }
  
  @Test(expected = GroupAccessException.class)
  public void testUnprotectWhenNotGroupMember() throws Exception {
    context.checking(findGroupMemberExpectations(returnValue(null)));
    service.unprotect(credential, protection);
  }
  
  @Test(expected = UserAccessException.class)
  public void testUnprotectWhenPasswordIncorrect() throws Exception {
    context.checking(findGroupMemberExpectations(returnValue(groupMember)));
    context.checking(accessGroupMemberExpectations());
    context.checking(accessCredentialKeyExpectations());
    context.checking(unwrapUserPrivateKeyExpectations(
        throwException(new IncorrectPassphraseException())));
    service.unprotect(credential, protection);
  }
  
  private Expectations findGroupMemberExpectations(final Action outcome) {
    return new Expectations() { { 
      allowing(protection).getLoginName();
      will(returnValue(loginName));
      allowing(credential).getOwner();
      will(returnValue(group));
      allowing(group).getName();
      will(returnValue(groupName));
      oneOf(groupMemberRepository).findByGroupAndLoginName(
          with(same(groupName)), with(same(loginName)));
      will(outcome);
    } };    
  }
  
  private Expectations accessCredentialKeyExpectations() { 
    return new Expectations() { { 
      oneOf(credential).getPrivateKey();
      will(returnValue(credentialKey));
      allowing(credentialKey).getContent();
      will(returnValue(encodedPrivateKey));
    } };
  }

  private Expectations accessGroupMemberExpectations() {
    return new Expectations() { { 
      allowing(groupMember).getUser();
      will(returnValue(user));
      allowing(user).getPrivateKey();
      will(returnValue(encodedPrivateKey));
      allowing(groupMember).getSecretKey();
      will(returnValue(encodedSecretKey));
    } };
  }
  
  private Expectations unwrapUserPrivateKeyExpectations(
      final Action outcome) {
    return new Expectations() { { 
      oneOf(protection).getPassword();
      will(returnValue(password));
      oneOf(pkcs8Decoder).decode(with(same(encodedPrivateKey)));
      will(returnValue(encryptedUserPrivateKey));
      oneOf(encryptedUserPrivateKey).setProtectionParameter(
          with(same(password)));
      oneOf(encryptedUserPrivateKey).derive();
      will(outcome);
    } };
  }
  
  private Expectations unwrapGroupSecretKeyExpectations() { 
    return new Expectations() { { 
      oneOf(secretKeyDecoder).decode(encodedSecretKey);
      will(returnValue(encryptedSecretKey));
      oneOf(encryptedSecretKey).setPrivateKey(userPrivateKey);
      oneOf(encryptedSecretKey).derive();
      will(returnValue(groupSecretKey));
    } };
  }

  private Expectations unwrapCredentialPrivateKeyExpectations() { 
    return new Expectations() { { 
      oneOf(aesDecoder).decode(encodedPrivateKey);
      will(returnValue(encryptedCredentialPrivateKey));
      oneOf(encryptedCredentialPrivateKey).setProtectionParameter(
          groupSecretKey);
      oneOf(encryptedCredentialPrivateKey).deriveWrapper();
      will(returnValue(credentialPrivateKey));
    } };
  }

  private Expectations wrapCredentialPrivateKeyExpectations() { 
    return new Expectations() { { 
      oneOf(privateKeyEncryptionService).encrypt(with(credentialPrivateKey), 
          with(groupSecretKey));
      will(returnValue(credentialPrivateKey));
    } };
  }

  private Expectations storeCredentialPrivateKeyExpectations() { 
    return new Expectations() { { 
      oneOf(credentialPrivateKey).getContent();
      will(returnValue(encodedPrivateKey));
      oneOf(credentialKey).setContent(with(encodedPrivateKey));
    } };
  }

}
