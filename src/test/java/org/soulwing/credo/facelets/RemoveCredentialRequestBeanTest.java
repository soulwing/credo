/*
 * File created on Mar 15, 2014 
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
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import javax.enterprise.context.Conversation;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.credential.NoSuchCredentialException;
import org.soulwing.credo.service.request.CredentialRequestDetail;
import org.soulwing.credo.service.request.RemoveCredentialRequestService;

/**
 * Unit tests for {@link RemoveCredentialRequestBean}.
 *
 * @author Carl Harris
 */
public class RemoveCredentialRequestBeanTest {

  private static final Long REQUEST_ID = -1L;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private RemoveCredentialRequestService requestService;
  
  @Mock
  private CredentialRequestDetail request;
  
  @Mock
  private Errors errors;
  
  @Mock
  private Conversation conversation;
  
  private RemoveCredentialRequestBean bean = new RemoveCredentialRequestBean();
  
  @Before
  public void setUp() throws Exception {
    bean.requestService = requestService;
    bean.errors = errors;
    bean.conversation = conversation;
  }

  @Test
  public void testFindRequestSuccess() throws Exception {
    context.checking(findRequestExpectations(returnValue(request), true));
    context.checking(beginConversationExpectations());
    bean.setId(REQUEST_ID);
    assertThat(bean.findRequest(), is(nullValue()));
    assertThat(bean.getRequest(), is(sameInstance(request)));
  }

  @Test
  public void testFindRequestWhenCredentialNotCreated() 
      throws Exception {
    context.checking(findRequestExpectations(returnValue(request), false));
    context.checking(beginConversationExpectations());
    context.checking(new Expectations() { { 
      oneOf(errors).addWarning(with("requestCredentialNotCreated"),
          with(emptyArray()));
    } });
    bean.setId(REQUEST_ID);
    assertThat(bean.findRequest(), is(nullValue()));
    assertThat(bean.getRequest(), is(sameInstance(request)));
  }

  @Test
  public void testFindRequestWhenNoSuchCredential() throws Exception {
    context.checking(findRequestExpectations(
        throwException(new NoSuchCredentialException()), false));
    context.checking(errorExpectations("id", "NotFound", REQUEST_ID));
    bean.setId(REQUEST_ID);
    assertThat(bean.findRequest(), 
        is(equalTo(RemoveCredentialRequestBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testFindRequestWhenNoId() throws Exception {
    context.checking(errorExpectations("id", "Required"));
    bean.setId(null);
    assertThat(bean.findRequest(), 
        is(equalTo(RemoveCredentialRequestBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(endConversationExpectations());
    assertThat(bean.cancel(), 
        is(equalTo(RemoveCredentialRequestBean.CANCEL_OUTCOME_ID)));
  }

  @Test
  public void testRemove() throws Exception {
    context.checking(removeCredentialExpectations(returnValue(null)));
    context.checking(endConversationExpectations());
    bean.setId(REQUEST_ID);
    assertThat(bean.remove(), 
        is(equalTo(RemoveCredentialRequestBean.SUCCESS_OUTCOME_ID)));
  }
  
  private Expectations findRequestExpectations(final Action outcome,
      final boolean credentialCreated) 
      throws Exception {
    return new Expectations() { { 
      oneOf(requestService).findRequestById(with(REQUEST_ID));
      will(outcome);
      allowing(request).isCredentialCreated();
      will(returnValue(credentialCreated));
    } };
  }
  
  private Expectations removeCredentialExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(requestService).removeRequest(with(REQUEST_ID));
      will(outcome);
    } };
  }

  private Expectations errorExpectations(final String id, 
      final String message) {
    return new Expectations() { { 
      oneOf(errors).addError(with(id), with(containsString(message)),
          with(emptyArray()));
    } };
  }

  private Expectations errorExpectations(final String id, 
      final String message, final Object obj) {
    return new Expectations() { { 
      oneOf(errors).addError(with(id), with(containsString(message)),
          with(arrayContaining(obj)));
    } };
  }

  private Expectations beginConversationExpectations() throws Exception {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
    } };
  }

  private Expectations endConversationExpectations() throws Exception {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(false));
      oneOf(conversation).end();
    } };
  }

}

