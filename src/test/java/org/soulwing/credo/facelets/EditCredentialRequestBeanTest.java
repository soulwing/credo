/*
 * File created on Mar 14, 2014 
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
import static org.hamcrest.Matchers.containsString;
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
import org.soulwing.credo.Password;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.credential.NoSuchCredentialException;
import org.soulwing.credo.service.request.CredentialRequestEditor;
import org.soulwing.credo.service.request.CredentialRequestException;
import org.soulwing.credo.service.request.EditCredentialRequestService;

/**
 * Unit tests for {@link EditCredentialRequestBean}.
 *
 * @author Carl Harris
 */
public class EditCredentialRequestBeanTest {

  private static final long REQUEST_ID = -1L;
  
  private static final Password PASSWORD = Password.EMPTY;
  
  private static final String GROUP_NAME = "someGroup";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private EditCredentialRequestService requestService;
  
  @Mock
  private CredentialRequestEditor editor;
  
  @Mock
  private Conversation conversation;
  
  @Mock
  private Errors errors;
  
  private EditCredentialRequestBean bean = new EditCredentialRequestBean();
  
  @Before
  public void setUp() throws Exception {
    bean.requestService = requestService;
    bean.errors = errors;
    bean.conversation = conversation;
    bean.editor = new DelegatingCredentialRequestEditor();
    bean.editor.setDelegate(this.editor);
    bean.passwordEditor = new PasswordFormEditor();
    bean.passwordEditor.setPassword(PASSWORD);
  }
  
  @Test
  public void testCreateEditor() throws Exception {
    context.checking(editRequestExpectations(returnValue(editor)));
    context.checking(beginConversationExpectations());
    bean.setId(REQUEST_ID);
    assertThat(bean.createEditor(), is(nullValue()));
    assertThat((CredentialRequestEditor) bean.getEditor().getDelegate(), 
        is(sameInstance(editor)));
  }

  @Test
  public void testCreateEditorWhenNoId() throws Exception {
    context.checking(errorExpectations("id", "Required"));
    assertThat(bean.createEditor(), is(nullValue()));
  }
  
  @Test
  public void testCreateEditorWhenRequestNotFound() throws Exception {
    context.checking(editRequestExpectations(
        throwException(new NoSuchCredentialException())));
    context.checking(errorExpectations("id", "NotFound", REQUEST_ID));    
    context.checking(endConversationExpectations());
    bean.setId(REQUEST_ID);
    assertThat(bean.createEditor(), 
        is(equalTo(EditCredentialRequestBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testSaveSuccess() throws Exception {
    context.checking(saveRequestExpectations(returnValue(null)));
    context.checking(endConversationExpectations());
    assertThat(bean.save(), 
        is(equalTo(EditCredentialRequestBean.SUCCESS_OUTCOME_ID)));
  }
  
  @Test
  public void testSaveWhenCredentialRequestException() throws Exception {
    context.checking(saveRequestExpectations(
        throwException(new CredentialRequestException())));
    assertThat(bean.save(), is(nullValue()));
  }

  @Test
  public void testSaveWhenNoSuchCredentialException() throws Exception {
    context.checking(saveRequestExpectations(
        throwException(new NoSuchCredentialException())));
    context.checking(endConversationExpectations());
    assertThat(bean.save(), 
        is(equalTo(EditCredentialRequestBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testSaveWhenGroupAccessException() throws Exception {
    context.checking(saveRequestExpectations(
        throwException(new GroupAccessException("message"))));
    context.checking(endConversationExpectations());
    assertThat(bean.save(), 
        is(equalTo(EditCredentialRequestBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testSaveWhenPassphraseException() throws Exception {
    context.checking(saveRequestExpectations(
        throwException(new PassphraseException())));
    context.checking(new Expectations() { { 
      oneOf(editor).getOwner();
      will(returnValue(GROUP_NAME));
    } });
    assertThat(bean.save(), 
        is(equalTo(EditCredentialRequestBean.PASSWORD_OUTCOME_ID)));
    assertThat(bean.getPasswordEditor().getGroupName(), 
        is(equalTo(GROUP_NAME)));
  }

  @Test
  public void testSaveWhenMergeConflict() throws Exception {
    context.checking(saveRequestExpectations(
        throwException(new MergeConflictException())));
    context.checking(editRequestExpectations(returnValue(editor)));
    context.checking(beginConversationExpectations());
    bean.setId(REQUEST_ID);
    assertThat(bean.save(), is(nullValue()));
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(endConversationExpectations());
    assertThat(bean.cancel(), 
        is(equalTo(EditCredentialRequestBean.CANCEL_OUTCOME_ID)));
  }
  
  private Expectations editRequestExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(requestService).editRequest(REQUEST_ID);
      will(outcome);
    } };
  }
  
  private Expectations saveRequestExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(editor).setPassword(with(PASSWORD));
      oneOf(requestService).saveRequest(with(same(editor)), 
          with(same(errors)));
      will(outcome);
    } };
  }
  
  private Expectations beginConversationExpectations() {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
    } };
  }

  private Expectations endConversationExpectations() {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(false));
      oneOf(conversation).end();
    } };
  }

  private Expectations errorExpectations(final String id, 
      final String message, final Object... args) {
    return new Expectations() { { 
      oneOf(errors).addError(with(id), with(containsString(message)),
          (Object[]) with(equalTo(args)));
    } };
  }

}
