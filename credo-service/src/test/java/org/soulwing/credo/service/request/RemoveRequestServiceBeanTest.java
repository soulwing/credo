/*
 * File created on Apr 12, 2014 
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.service.NoSuchCredentialException;

/**
 * Unit tests for {@link RemoveRequestServiceBean}.
 *
 * @author Carl Harris
 */
public class RemoveRequestServiceBeanTest {

  private static final long REQUEST_ID = -1L;

  private static final String REQUEST_NAME = "requestName";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private CredentialRequestRepository requestRepository;

  @Mock
  private Credential credential;
  
  @Mock
  private CredentialRequest request;
  
  private RemoveRequestServiceBean service = new RemoveRequestServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.credentialRepository = credentialRepository;
    service.requestRepository = requestRepository;
  }
  
  @Test
  public void testFindRequestById() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(request));
      oneOf(credentialRepository).findByRequestId(with(REQUEST_ID));
      will(returnValue(credential));
      allowing(request).getName();
      will(returnValue(REQUEST_NAME));
    } });
    
    CredentialRequestDetail detail = service.findRequestById(REQUEST_ID);
    assertThat(detail.getName(), is(equalTo(REQUEST_NAME)));
    assertThat(detail.isCredentialCreated(), is(equalTo(true)));
  }

  @Test
  public void testFindRequestByIdWhenCredentialNotCreated() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(request));
      oneOf(credentialRepository).findByRequestId(with(REQUEST_ID));
      will(returnValue(null));
      allowing(request).getName();
      will(returnValue(REQUEST_NAME));
    } });
    
    CredentialRequestDetail detail = service.findRequestById(REQUEST_ID);
    assertThat(detail.getName(), is(equalTo(REQUEST_NAME)));
    assertThat(detail.isCredentialCreated(), is(equalTo(false)));
  }
  
  @Test(expected = NoSuchCredentialException.class)
  public void testFindRequestByIdWhenRequestNotFound() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(null));
    } });
    
    service.findRequestById(REQUEST_ID);
  }
  
  @Test
  public void testRemoveRequest() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).findByRequestId(with(REQUEST_ID));
      will(returnValue(credential));
      oneOf(credential).setRequest(with(nullValue(CredentialRequest.class)));
      oneOf(credentialRepository).update(with(same(credential)));
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(request));
      oneOf(requestRepository).remove(with(same(request)), with(false));
    } });
    
    service.removeRequest(REQUEST_ID);
  }

  @Test
  public void testRemoveRequestWhenNoCredentialCreated() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).findByRequestId(with(REQUEST_ID));
      will(returnValue(null));
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(request));
      oneOf(requestRepository).remove(with(same(request)), with(true));
    } });
    
    service.removeRequest(REQUEST_ID);
  }
  
}
