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
import org.soulwing.credo.Credential;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.credential.NoSuchCredentialException;
import org.soulwing.credo.service.credential.RemoveCredentialService;

/**
 * Unit tests for {@link RemoveCredentialBean}.
 *
 * @author Carl Harris
 */
public class RemoveCredentialBeanTest {

  private static final Long CREDENTIAL_ID = -1L;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private RemoveCredentialService credentialService;
  
  @Mock
  private Credential credential;
  
  @Mock
  private Errors errors;
  
  @Mock
  private Conversation conversation;
  
  private RemoveCredentialBean bean = new RemoveCredentialBean();
  
  @Before
  public void setUp() throws Exception {
    bean.credentialService = credentialService;
    bean.errors = errors;
    bean.conversation = conversation;
  }

  @Test
  public void testFindCredentialSuccess() throws Exception {
    context.checking(findCredentialExpectations(returnValue(credential)));
    context.checking(beginConversationExpectations());
    bean.setId(CREDENTIAL_ID);
    assertThat(bean.findCredential(), is(nullValue()));
    assertThat(bean.getCredential(), is(sameInstance(credential)));
  }

  @Test
  public void testFindCredentialWhenNoSuchCredential() throws Exception {
    context.checking(findCredentialExpectations(
        throwException(new NoSuchCredentialException())));
    context.checking(errorExpectations("id", "NotFound", CREDENTIAL_ID));
    bean.setId(CREDENTIAL_ID);
    assertThat(bean.findCredential(), 
        is(equalTo(RemoveCredentialBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testFindCredentialWhenNoId() throws Exception {
    context.checking(errorExpectations("id", "Required"));
    bean.setId(null);
    assertThat(bean.findCredential(), 
        is(equalTo(RemoveCredentialBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(endConversationExpectations());
    assertThat(bean.cancel(), 
        is(equalTo(RemoveCredentialBean.CANCEL_OUTCOME_ID)));
  }

  @Test
  public void testRemove() throws Exception {
    context.checking(removeCredentialExpectations(returnValue(null)));
    context.checking(endConversationExpectations());
    bean.setId(CREDENTIAL_ID);
    assertThat(bean.remove(), 
        is(equalTo(RemoveCredentialBean.SUCCESS_OUTCOME_ID)));
  }
  
  private Expectations findCredentialExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(credentialService).findCredentialById(with(CREDENTIAL_ID));
      will(outcome);
    } };
  }
  
  private Expectations removeCredentialExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(credentialService).removeCredential(with(CREDENTIAL_ID));
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

