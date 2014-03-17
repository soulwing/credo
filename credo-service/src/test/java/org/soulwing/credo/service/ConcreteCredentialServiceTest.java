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
package org.soulwing.credo.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.repository.CredentialRepository;

/**
 * Unit tests for {@link ConcreteCredentialService}.
 *
 * @author Carl Harris
 */
public class ConcreteCredentialServiceTest {

  private static final String LOGIN_NAME = "loginName";

  private static final Long CREDENTIAL_ID = -1L;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Credential credential;
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private UserContextService userContextService;
  
  private ConcreteCredentialService service = new ConcreteCredentialService();
  
  @Before
  public void setUp() throws Exception {
    service.credentialRepository = credentialRepository;
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
  public void testFindAllCredentialsByLoginName() throws Exception {
    context.checking(new Expectations() { {
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(credentialRepository).findAllByLoginName(with(LOGIN_NAME));
      will(returnValue(Collections.singletonList(credential)));
    } });
    
    List<Credential> credentials = service.findAllCredentials();
    assertThat(credentials, is(not(empty())));
    assertThat(credentials.get(0), is(sameInstance(credential)));
  }
  
  @Test
  public void testRemoveCredential() throws Exception {
    context.checking(new Expectations() { {
      oneOf(credentialRepository).remove(with(CREDENTIAL_ID));
    } });
    
    service.removeCredential(CREDENTIAL_ID);
  }

}
