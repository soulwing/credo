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

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Credential credential;
  
  @Mock
  private CredentialRepository credentialRepository;
  
  private ConcreteCredentialService service = new ConcreteCredentialService();
  
  @Before
  public void setUp() throws Exception {
    service.credentialRepository = credentialRepository;
  }
  
  @Test
  public void testFindCredentialById() throws Exception {
    final Long id = -1L;
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).findById(with(same(id)));
      will(returnValue(credential));
    } });
    
    assertThat(service.findCredentialById(id), is(sameInstance(credential)));
  }

  @Test(expected = NoSuchCredentialException.class)
  public void testFindCredentialByIdNotFound() throws Exception {
    final Long id = -1L;
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).findById(with(same(id)));
      will(returnValue(null));
    } });

    service.findCredentialById(id);
  }

  @Test
  public void testFindAllCredentials() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).findAll();
      will(returnValue(Collections.singletonList(credential)));
    } });
    
    List<Credential> credentials = service.findAllCredentials();
    assertThat(credentials, is(not(empty())));
    assertThat(credentials.get(0), is(sameInstance(credential)));
  }
  
}
