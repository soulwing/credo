/*
 * File created on Mar 20, 2014 
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
package org.soulwing.credo.service.request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.Collection;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.UserContextService;

/**
 * Unit tests for {@link CredentialRequestServiceBean}.
 *
 * @author Carl Harris
 */
public class CredentialRequestServiceBeanTest {

  private static final String LOGIN_NAME = "someUser";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private CredentialRequestRepository requestRepository;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  private CredentialRequest request;
  
  @Mock
  private UserGroup group1;
  
  @Mock
  private UserGroup group2;
  
  private CredentialRequestServiceBean service =
      new CredentialRequestServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.groupRepository = groupRepository;
    service.requestRepository = requestRepository;
    service.userContextService = userContextService;
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllRequests() throws Exception {
    context.checking(new Expectations() { {
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(groupRepository).findByLoginName(with(LOGIN_NAME));
      will(returnValue(Collections.singletonList(group1)));
      oneOf(groupRepository).findDescendants(with(same(group1)));
      will(returnValue(Collections.singletonList(group2)));
      oneOf(requestRepository).findAllByOwners(
          (Collection<UserGroup>) with(contains(group1, group2)));
      will(returnValue(Collections.singletonList(request)));
    } });
    
    assertThat(service.findAllRequests(), contains(request));
  }
  
}
