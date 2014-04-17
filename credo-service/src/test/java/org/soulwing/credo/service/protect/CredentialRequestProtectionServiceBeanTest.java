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

import java.security.PrivateKey;

import javax.crypto.SecretKey;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.CredentialKey;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.group.NoSuchGroupException;

/**
 * Unit tests for {@link ConcreteRequestProtectionService}.
 *
 * @author Carl Harris
 */
public class CredentialRequestProtectionServiceBeanTest {

  private static final long REQUEST_ID = -1L;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  private static final String LOGIN_NAME = "someUser";
  
  private static final String GROUP_NAME = "someGroup";

  private static final Password PASSWORD = Password.EMPTY;

  @Mock
  private UserGroupRepository groupRepository; 
  
  @Mock
  private GroupProtectionService groupProtectionService;
  
  @Mock
  private PrivateKeyDecoder aesDecoder;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  private PrivateKeyEncryptionService privateKeyEncryptionService;
  
  @Mock
  private CredentialRequest request;

  @Mock
  private ProtectionParameters protection;
  
  @Mock
  private CredentialKey credentialKey;

  @Mock
  private UserGroup group;

  @Mock
  private UserGroup otherGroup;
  
  @Mock
  private UserProfile user;
  
  @Mock
  private UserGroupMember groupMember;
  
  @Mock
  private PrivateKey userPrivateKey;
  
  @Mock
  private SecretKeyWrapper groupSecretKeyWrapper;
  
  @Mock
  private SecretKey groupSecretKey;
  
  @Mock
  private PrivateKeyWrapper credentialPrivateKey;
  
  private CredentialRequestProtectionServiceBean service = 
      new CredentialRequestProtectionServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.groupRepository = groupRepository;
    service.groupProtectionService = groupProtectionService;
    service.aesDecoder = aesDecoder;
    service.userContextService = userContextService;
    service.privateKeyEncryptionService = privateKeyEncryptionService;
  }

  @Test
  public void testProtectSuccess() throws Exception {
    context.checking(groupExpectations(returnValue(group)));
    context.checking(groupUnprotectExpectations());
    context.checking(wrapCredentialPrivateKeyExpectations());
    service.protect(request, credentialPrivateKey, protection);
  }
  
  @Test(expected = NoSuchGroupException.class)
  public void testProtectWhenGroupDoesNotExist() throws Exception {
    context.checking(groupExpectations(returnValue(null)));
    service.protect(request, credentialPrivateKey, protection);
  }
  
  @Test
  public void testUnprotectSuccess() throws Exception {
    context.checking(groupExpectations(returnValue(group)));
    context.checking(groupUnprotectExpectations());
    context.checking(checkOwnershipExpectations(returnValue(group)));
    context.checking(unwrapCredentialPrivateKeyExpectations());
    service.unprotect(request, protection);
  }
  
  @Test(expected = RuntimeException.class)
  public void testUnprotectWhenNotSameOwnerGroup() throws Exception {
    context.checking(groupExpectations(returnValue(group)));
    context.checking(checkOwnershipExpectations(returnValue(otherGroup)));
    service.unprotect(request, protection);
  }
  
  private Expectations groupExpectations(final Action outcome) {
    return new Expectations() { {
      allowing(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      allowing(protection).getGroupName();
      will(returnValue(GROUP_NAME));
      oneOf(groupRepository).findByGroupName(
          with(same(GROUP_NAME)), with(same(LOGIN_NAME)));
      will(outcome);
      allowing(request).getId();
      will(returnValue(REQUEST_ID));
    } };    
  }
  
  private Expectations groupUnprotectExpectations() throws Exception {
    return new Expectations() { {
      oneOf(protection).getPassword();
      will(returnValue(PASSWORD));
      oneOf(groupProtectionService).unprotect(with(same(group)), 
          with(same(PASSWORD)));
      will(returnValue(groupSecretKeyWrapper));
      oneOf(groupSecretKeyWrapper).derive();
      will(returnValue(groupSecretKey));
    } };
  }
  
  private Expectations checkOwnershipExpectations(final Action outcome) {
    return new Expectations() { { 
      allowing(groupMember).getGroup();
      will(returnValue(group));
      oneOf(request).getOwner();
      will(outcome);
      allowing(request).getName();
      will(returnValue("some credential name"));
    } };
  }
  
  private Expectations unwrapCredentialPrivateKeyExpectations() { 
    final String encodedPrivateKey = new String();
    return new Expectations() { { 
      oneOf(request).getPrivateKey();
      will(returnValue(credentialKey));
      allowing(credentialKey).getContent();
      will(returnValue(encodedPrivateKey));
      oneOf(aesDecoder).decode(encodedPrivateKey);
      will(returnValue(credentialPrivateKey));
      oneOf(credentialPrivateKey).setProtectionParameter(
          groupSecretKey);
      oneOf(credentialPrivateKey).deriveWrapper();
      will(returnValue(credentialPrivateKey));
    } };
  }

  private Expectations wrapCredentialPrivateKeyExpectations() {
    final String encodedPrivateKey = new String();
    return new Expectations() { {
      oneOf(privateKeyEncryptionService).encrypt(with(credentialPrivateKey), 
          with(groupSecretKey));
      will(returnValue(credentialPrivateKey));
      oneOf(credentialPrivateKey).getContent();
      will(returnValue(encodedPrivateKey));
      oneOf(request).setOwner(with(same(group)));
      oneOf(request).getPrivateKey();
      will(returnValue(credentialKey));
      oneOf(credentialKey).setContent(with(same(encodedPrivateKey)));
    } };
  }

}
