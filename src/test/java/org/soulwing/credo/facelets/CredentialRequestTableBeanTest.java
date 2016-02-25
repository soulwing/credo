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
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.request.CredentialRequestService;
import org.soulwing.credo.testing.JUnitRuleClassImposterizingMockery;

/**
 * Unit tests for {@link CredentialRequestTableBean}.
 *
 * @author Carl Harris
 */
public class CredentialRequestTableBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleClassImposterizingMockery();
  
  @Mock
  private CredentialRequest request;
  
  @Mock
  private CredentialRequestService requestService;
  
  private CredentialRequestTableBean bean = new CredentialRequestTableBean();
  
  @Before
  public void setUp() throws Exception {
    bean.requestService = requestService;
  }
  
  @Test
  public void testGetRequests() throws Exception {
    context.checking(new Expectations() { {
      oneOf(requestService).findAllRequests();
      will(returnValue(Collections.singletonList(request)));
    } });
    
    List<CredentialRequest> credentials = bean.getRequests();
    assertThat(credentials, not(empty()));
    assertThat(credentials.get(0), sameInstance(request));
  }
  
}
