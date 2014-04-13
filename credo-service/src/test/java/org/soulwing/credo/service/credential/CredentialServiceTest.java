/*
 * File created on Feb 21, 2014 
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
package org.soulwing.credo.service.credential;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.credential.CredentialServiceBean;

/**
 * Unit tests for {@link CredentialServiceBean}.
 *
 * @author Carl Harris
 */
public class CredentialServiceTest {

  private static final String LOGIN_NAME = "loginName";

  private static final Long CREDENTIAL_ID = -1L;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Credential credential;
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  private UserGroup group1;

  @Mock
  private UserGroup group2;

  private CredentialServiceBean service = new CredentialServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.credentialRepository = credentialRepository;
    service.groupRepository = groupRepository;
    service.userContextService = userContextService;
  }
  
  @Test
  public void testFindCredentialById() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).findById(with(same(CREDENTIAL_ID)));
      will(returnValue(credential));
    } });
    
    assertThat(service.findCredentialById(CREDENTIAL_ID), 
        is(sameInstance(credential)));
  }

  @Test(expected = NoSuchCredentialException.class)
  public void testFindCredentialByIdNotFound() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).findById(with(same(CREDENTIAL_ID)));
      will(returnValue(null));
    } });

    service.findCredentialById(CREDENTIAL_ID);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllCredentials() throws Exception {
    context.checking(new Expectations() { {
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(groupRepository).findByLoginName(with(LOGIN_NAME));
      will(returnValue(Collections.singletonList(group1)));
      oneOf(groupRepository).findDescendants(with(same(group1)));
      will(returnValue(Collections.singletonList(group2)));
      oneOf(credentialRepository).findAllByOwners(
          (Collection<UserGroup>) with(contains(group1, group2)));
      will(returnValue(Collections.singletonList(credential)));
    } });
    
    List<Credential> credentials = service.findAllCredentials();
    assertThat(credentials, is(not(empty())));
    assertThat(credentials.get(0), is(sameInstance(credential)));
  }
  
  @Test
  public void testRemoveCredential() throws Exception {
    context.checking(new Expectations() { {
      oneOf(credentialRepository).findById(with(CREDENTIAL_ID));
      will(returnValue(credential));
      oneOf(credentialRepository).remove(with(credential));
    } });
    
    service.removeCredential(CREDENTIAL_ID);
  }

}
