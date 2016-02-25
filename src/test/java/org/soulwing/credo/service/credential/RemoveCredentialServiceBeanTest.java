/*
 * File created on Apr 14, 2014 
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.repository.CredentialRepository;

/**
 * Unit tests for {@link RemoveCredentialServiceBean}
 *
 * @author Carl Harris
 */
public class RemoveCredentialServiceBeanTest {

  private static final Long CREDENTIAL_ID = -1L;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private Credential credential;
  
  private RemoveCredentialServiceBean service = 
      new RemoveCredentialServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.credentialRepository = credentialRepository;
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
  public void testRemoveCredential() throws Exception {
    context.checking(new Expectations() { {
      oneOf(credentialRepository).findById(with(CREDENTIAL_ID));
      will(returnValue(credential));
      oneOf(credentialRepository).remove(with(credential));
    } });
    
    service.removeCredential(CREDENTIAL_ID);
  }

}
