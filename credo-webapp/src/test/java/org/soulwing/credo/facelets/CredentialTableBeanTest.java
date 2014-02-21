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
package org.soulwing.credo.facelets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
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
import org.soulwing.credo.service.CredentialService;

/**
 * Unit tests for {@link CredentialTableBean}.
 *
 * @author Carl Harris
 */
public class CredentialTableBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  public Credential credential;
  
  @Mock
  public CredentialService credentialService;
  
  public CredentialTableBean bean = new CredentialTableBean();
  
  @Before
  public void setUp() throws Exception {
    bean.credentialService = credentialService;
  }
  
  @Test
  public void testGetCredentials() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credentialService).findAllCredentials();
      will(returnValue(Collections.singletonList(credential)));
    } });
    
    List<CredentialBean> credentials = bean.getCredentials();
    assertThat(credentials, not(empty()));
    assertThat(credentials.get(0).getDelegate(), sameInstance(credential));
  }
  
}
